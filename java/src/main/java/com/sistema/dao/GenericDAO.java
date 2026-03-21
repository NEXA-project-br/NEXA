package com.sistema.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica de acesso a dados (DAO).
 * Define o contrato CRUD que todas as implementações devem respeitar.
 *
 * @param <T>  Tipo da entidade gerenciada
 */
public interface GenericDAO<T> {

    /**
     * Persiste uma nova entidade no banco de dados.
     * @param entidade a ser salva
     * @return a entidade com ID gerado
     */
    T salvar(T entidade);

    /**
     * Atualiza uma entidade existente no banco de dados.
     * @param entidade com dados atualizados
     * @return a entidade gerenciada e atualizada
     */
    T atualizar(T entidade);

    /**
     * Remove uma entidade do banco de dados pelo seu ID.
     * @param id identificador único da entidade
     */
    void excluir(Long id);

    /**
     * Busca uma entidade pelo seu identificador único.
     * @param id identificador único
     * @return Optional contendo a entidade, ou vazio se não encontrada
     */
    Optional<T> buscarPorId(Long id);

    /**
     * Retorna todas as entidades do tipo T persistidas no banco.
     * @return lista imutável de entidades
     */
    List<T> listarTodos();
}