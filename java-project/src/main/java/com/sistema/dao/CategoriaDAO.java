package com.sistema.dao;

import com.sistema.model.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * DAO específico para a entidade {@link Categoria}.
 * Herda todas as operações CRUD de {@link BaseDAOImpl} e adiciona
 * consultas específicas do domínio de categorias.
 */
public class CategoriaDAO extends BaseDAOImpl<Categoria> {

    public CategoriaDAO() {
        super(Categoria.class);
    }

    /**
     * Busca uma categoria pelo nome (case-insensitive).
     *
     * @param nome nome a pesquisar
     * @return Optional com a categoria encontrada
     */
    public Optional<Categoria> buscarPorNome(String nome) {
        return executarLeitura(em -> {
            var query = em.createQuery(
                    "SELECT c FROM Categoria c WHERE LOWER(c.nome) = LOWER(:nome)",
                    Categoria.class);
            query.setParameter("nome", nome);
            return query.getResultStream().findFirst();
        });
    }

    /**
     * Lista todas as categorias ordenadas por nome.
     *
     * @return lista de categorias em ordem alfabética
     */
    public List<Categoria> listarOrdenadoPorNome() {
        return executarLeitura(em ->
                em.createQuery("SELECT c FROM Categoria c ORDER BY c.nome ASC", Categoria.class)
                        .getResultList());
    }

    /**
     * Lista categorias filtradas por tipo (RECEITA ou DESPESA), em ordem alfabética.
     */
    public List<Categoria> listarPorTipo(com.sistema.model.TipoTransacao tipo) {
        return executarLeitura(em ->
                em.createQuery(
                        "SELECT c FROM Categoria c WHERE c.tipo = :tipo ORDER BY c.nome ASC",
                        Categoria.class)
                  .setParameter("tipo", tipo)
                  .getResultList());
    }

    /**
     * Verifica se uma categoria possui transações vinculadas.
     * Usado para impedir exclusão de categorias em uso.
     *
     * @param categoriaId ID da categoria
     * @return true se existirem transações associadas
     */
    public boolean possuiTransacoes(Long categoriaId) {
        return executarLeitura(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(t) FROM Transacao t WHERE t.categoria.id = :id",
                            Long.class)
                    .setParameter("id", categoriaId)
                    .getSingleResult();
            return count > 0;
        });
    }
}