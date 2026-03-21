package com.sistema.controller;

import com.sistema.dao.CategoriaDAO;
import com.sistema.model.Categoria;

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

        // Regra de negócio: impede exclusão se houver transações vinculadas
        if (categoriaDAO.possuiTransacoes(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir uma categoria que possui transações vinculadas.");
        }

        categoriaDAO.excluir(id);
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
     * Salva apenas pelo nome, criando um objeto Categoria internamente.
     *
     * @param nome nome da nova categoria
     * @return categoria persistida
     */
    public Categoria salvarPorNome(String nome) {
        return salvar(new Categoria(nome != null ? nome.trim() : null));
    }

    // ── Validação ─────────────────────────────────────────────────────────────

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }
        if (categoria.getNome().length() > 100) {
            throw new IllegalArgumentException("O nome da categoria não pode ter mais de 100 caracteres.");
        }
    }
}