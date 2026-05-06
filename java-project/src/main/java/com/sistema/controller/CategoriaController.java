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

    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    // ── Implementação GenericController ───────────────────────────────────────

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

    @Override
    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }
        categoriaDAO.excluirDesvinculandoTransacoes(id);
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaDAO.buscarPorId(id);
    }

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
     * Conta quantas transacoes ficarao sem categoria se a categoria for excluida.
     */
    public long contarTransacoesVinculadas(Long categoriaId) {
        if (categoriaId == null) {
            return 0;
        }
        return categoriaDAO.contarTransacoes(categoriaId);
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
