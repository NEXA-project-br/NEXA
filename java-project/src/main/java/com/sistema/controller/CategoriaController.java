package com.sistema.controller;

import com.sistema.dao.CategoriaDAO;
import com.sistema.model.Categoria;
import com.sistema.model.TipoTransacao;

import java.util.List;
import java.util.Optional;

/**
 * Controller de negócio para a entidade {@link Categoria}.
 * Aplica validações e regras de negócio antes de delegar ao DAO.
 */
public class CategoriaController implements GenericController<Categoria> {

    /**
     * DAO responsavel pela persistencia de categorias.
     */
    private final CategoriaDAO categoriaDAO;

    /**
     * Cria o controller com uma instancia padrao de {@link CategoriaDAO}.
     */
    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    // ── Implementação GenericController ───────────────────────────────────────

    /**
     * Executa a rotina salvar.
     *
     * @param categoria parametro categoria
     * @return resultado da operacao
     */
    @Override
    public Categoria salvar(Categoria categoria) {
        validarCategoria(categoria);

        // Impede nome duplicado
        categoriaDAO.buscarPorNome(categoria.getNome())
                .ifPresent(c -> {
                    throw new IllegalArgumentException(
                            "Já existe uma categoria com o nome: " + categoria.getNome());
                });

        return categoriaDAO.salvar(categoria);
    }

    /**
     * Executa a rotina atualizar.
     *
     * @param categoria parametro categoria
     * @return resultado da operacao
     */
    @Override
    public Categoria atualizar(Categoria categoria) {
        validarCategoria(categoria);

        if (categoria.getId() == null) {
            throw new IllegalArgumentException("ID da categoria não pode ser nulo para atualização.");
        }

        // Verifica se o novo nome não conflita com outro registro
        categoriaDAO.buscarPorNome(categoria.getNome())
                .filter(c -> !c.getId().equals(categoria.getId()))
                .ifPresent(c -> {
                    throw new IllegalArgumentException(
                            "Já existe outra categoria com o nome: " + categoria.getNome());
                });

        return categoriaDAO.atualizar(categoria);
    }

    /**
     * Executa a rotina excluir.
     *
     * @param id parametro id
     */
    @Override
    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }

        // Regra de negócio: impede exclusão se houver transações vinculadas
        if (categoriaDAO.possuiTransacoes(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir uma categoria que possui transações vinculadas.");
        }

        categoriaDAO.excluir(id);
    }

    /**
     * Executa a rotina buscarPorId.
     *
     * @param id parametro id
     * @return resultado da operacao
     */
    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaDAO.buscarPorId(id);
    }

    /**
     * Executa a rotina listarTodos.
     *
     * @return resultado da operacao
     */
    @Override
    public List<Categoria> listarTodos() {
        return categoriaDAO.listarOrdenadoPorNome();
    }

    // ── Métodos específicos ───────────────────────────────────────────────────

    /**
     * Lista categorias de um tipo específico em ordem alfabética.
     */
    public List<Categoria> listarPorTipo(TipoTransacao tipo) {
        return categoriaDAO.listarPorTipo(tipo);
    }

    /**
     * Salva apenas pelo nome e tipo, criando um objeto Categoria internamente.
     */
    public Categoria salvarPorNome(String nome, TipoTransacao tipo) {
        return salvar(new Categoria(nome != null ? nome.trim() : null, tipo));
    }

    /**
     * Salva apenas pelo nome, criando um objeto Categoria internamente.
     *
     * @param nome nome da nova categoria
     * @return categoria persistida
     * @deprecated Use {@link #salvarPorNome(String, TipoTransacao)} para informar o tipo.
     */
    @Deprecated
    public Categoria salvarPorNome(String nome) {
        return salvar(new Categoria(nome != null ? nome.trim() : null, TipoTransacao.DESPESA));
    }

    // ── Validação ─────────────────────────────────────────────────────────────

    /**
     * Executa a rotina validarCategoria.
     *
     * @param categoria parametro categoria
     */
    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria e obrigatorio.");
        }
        if (categoria.getNome().length() > 100) {
            throw new IllegalArgumentException("O nome da categoria nao pode ter mais de 100 caracteres.");
        }
        if (categoria.getTipo() == null) {
            throw new IllegalArgumentException("O tipo da categoria (RECEITA/DESPESA) e obrigatorio.");
        }
    }
}
