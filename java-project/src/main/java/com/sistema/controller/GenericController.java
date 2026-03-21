package com.sistema.controller;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica de controle de negócio.
 * Define o contrato entre a camada View e a camada de persistência,
 * abstraindo a lógica de negócio de forma reutilizável.
 *
 * @param <T>  Tipo da entidade gerenciada
 */
public interface GenericController<T> {

    /**
     * Salva uma nova entidade aplicando validações de negócio.
     * @param entidade a ser persistida
     * @return entidade persistida com ID gerado
     * @throws IllegalArgumentException se a entidade for inválida
     */
    T salvar(T entidade);

    /**
     * Atualiza uma entidade existente.
     * @param entidade com os dados atualizados
     * @return entidade atualizada
     */
    T atualizar(T entidade);

    /**
     * Remove uma entidade pelo ID, aplicando regras de negócio.
     * @param id identificador da entidade
     */
    void excluir(Long id);

    /**
     * Busca uma entidade por ID.
     * @param id identificador
     * @return Optional com a entidade, ou vazio
     */
    Optional<T> buscarPorId(Long id);

    /**
     * Lista todas as entidades do tipo T.
     * @return lista de entidades
     */
    List<T> listarTodos();
}