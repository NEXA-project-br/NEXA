package com.sistema.dao;

import com.sistema.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Implementação base genérica do padrão DAO.
 * Centraliza as operações CRUD comuns, eliminando duplicação de código
 * nas classes DAO específicas.
 *
 * @param <T>  Tipo da entidade gerenciada
 */
public abstract class BaseDAOImpl<T> implements GenericDAO<T> {

    /** Classe da entidade gerenciada — necessária para TypedQuery e find(). */
    protected final Class<T> classeEntidade;

    /**
     * Cria uma nova instancia de BaseDAOImpl.
     *
     * @param classeEntidade parametro classeEntidade
     */
    protected BaseDAOImpl(Class<T> classeEntidade) {
        this.classeEntidade = classeEntidade;
    }

    // ── Template para gerenciar transações ───────────────────────────────────

    /**
     * Executa uma operação de escrita dentro de uma transação gerenciada.
     * Faz rollback automático em caso de erro.
     */
    protected <R> R executarEmTransacao(TransacaoCallback<R> callback) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = callback.executar(em);
            tx.commit();
            return resultado;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Erro ao executar operação no banco de dados", e);
        } finally {
            em.close();
        }
    }

    /**
     * Executa uma operação de leitura sem transação explícita.
     */
    protected <R> R executarLeitura(LeituraCallback<R> callback) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return callback.executar(em);
        } catch (Exception e) {
            throw new DAOException("Erro ao executar leitura no banco de dados", e);
        } finally {
            em.close();
        }
    }

    // ── Implementações CRUD ───────────────────────────────────────────────────

    /**
     * Executa a rotina salvar.
     *
     * @param entidade parametro entidade
     * @return resultado da operacao
     */
    @Override
    public T salvar(T entidade) {
        return executarEmTransacao(em -> {
            em.persist(entidade);
            return entidade;
        });
    }

    /**
     * Executa a rotina atualizar.
     *
     * @param entidade parametro entidade
     * @return resultado da operacao
     */
    @Override
    public T atualizar(T entidade) {
        return executarEmTransacao(em -> em.merge(entidade));
    }

    /**
     * Executa a rotina excluir.
     *
     * @param id parametro id
     */
    @Override
    public void excluir(Long id) {
        executarEmTransacao(em -> {
            T entidade = em.find(classeEntidade, id);
            if (entidade != null) {
                em.remove(entidade);
            }
            return null;
        });
    }

    /**
     * Executa a rotina buscarPorId.
     *
     * @param id parametro id
     * @return resultado da operacao
     */
    @Override
    public Optional<T> buscarPorId(Long id) {
        return executarLeitura(em -> Optional.ofNullable(em.find(classeEntidade, id)));
    }

    /**
     * Executa a rotina listarTodos.
     *
     * @return resultado da operacao
     */
    @Override
    public List<T> listarTodos() {
        return executarLeitura(em -> {
            String jpql = "SELECT e FROM " + classeEntidade.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, classeEntidade);
            return query.getResultList();
        });
    }

    // ── Interfaces funcionais internas ────────────────────────────────────────

    /**
     * Tipo responsavel por funcionalidades de TransacaoCallback.
     */
    @FunctionalInterface
    protected interface TransacaoCallback<R> {
        R executar(EntityManager em) throws Exception;
    }

    /**
     * Tipo responsavel por funcionalidades de LeituraCallback.
     */
    @FunctionalInterface
    protected interface LeituraCallback<R> {
        R executar(EntityManager em) throws Exception;
    }

    // ── Exceção de DAO ────────────────────────────────────────────────────────

    /**
     * Tipo responsavel por funcionalidades de DAOException.
     */
    public static class DAOException extends RuntimeException {
        /**
         * Cria uma nova excecao de DAO.
         *
         * @param message mensagem da excecao
         * @param cause causa original da excecao
         */
        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
