package com.sistema.dao;

import com.sistema.model.TipoTransacao;
import com.sistema.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO específico para a entidade {@link Transacao}.
 * Fornece consultas avançadas para filtros, somatórios e relatórios.
 */
public class TransacaoDAO extends BaseDAOImpl<Transacao> {

    /**
     * Cria uma nova instancia de TransacaoDAO.
     */
    public TransacaoDAO() {
        super(Transacao.class);
    }

    /**
     * Lista todas as transações ordenadas por data decrescente.
     */
    public List<Transacao> listarOrdenadoPorData() {
        return executarLeitura(em ->
                em.createQuery(
                                "SELECT t FROM Transacao t LEFT JOIN FETCH t.categoria " +
                                "ORDER BY t.data DESC",
                                Transacao.class)
                        .getResultList());
    }

    /**
     * Lista as N transações mais recentes.
     *
     * @param limite número máximo de registros
     */
    public List<Transacao> listarUltimas(int limite) {
        return executarLeitura(em ->
                em.createQuery(
                                "SELECT t FROM Transacao t LEFT JOIN FETCH t.categoria " +
                                "ORDER BY t.data DESC",
                                Transacao.class)
                        .setMaxResults(limite)
                        .getResultList());
    }

    /**
     * Filtra transações em um intervalo de datas.
     *
     * @param inicio data inicial (inclusive)
     * @param fim    data final (inclusive)
     */
    public List<Transacao> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return executarLeitura(em ->
                em.createQuery(
                                "SELECT t FROM Transacao t LEFT JOIN FETCH t.categoria " +
                                "WHERE t.data BETWEEN :inicio AND :fim " +
                                "ORDER BY t.data DESC",
                                Transacao.class)
                        .setParameter("inicio", inicio)
                        .setParameter("fim", fim)
                        .getResultList());
    }

    /**
     * Filtra por período e tipo (RECEITA ou DESPESA).
     */
    public List<Transacao> filtrarPorPeriodoETipo(LocalDate inicio, LocalDate fim,
                                                   TipoTransacao tipo) {
        return executarLeitura(em ->
                em.createQuery(
                                "SELECT t FROM Transacao t LEFT JOIN FETCH t.categoria " +
                                "WHERE t.data BETWEEN :inicio AND :fim " +
                                "AND t.tipo = :tipo " +
                                "ORDER BY t.data DESC",
                                Transacao.class)
                        .setParameter("inicio", inicio)
                        .setParameter("fim", fim)
                        .setParameter("tipo", tipo)
                        .getResultList());
    }

    /**
     * Soma os valores de um tipo específico em um período.
     *
     * @param tipo   RECEITA ou DESPESA
     * @param inicio data inicial
     * @param fim    data final
     * @return soma dos valores ou ZERO se não houver registros
     */
    public BigDecimal somarPorTipoEPeriodo(TipoTransacao tipo,
                                            LocalDate inicio, LocalDate fim) {
        return executarLeitura(em -> {
            BigDecimal resultado = em.createQuery(
                            "SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t " +
                            "WHERE t.tipo = :tipo " +
                            "AND t.data BETWEEN :inicio AND :fim",
                            BigDecimal.class)
                    .setParameter("tipo", tipo)
                    .setParameter("inicio", inicio)
                    .setParameter("fim", fim)
                    .getSingleResult();
            return resultado != null ? resultado : BigDecimal.ZERO;
        });
    }

    /**
     * Soma total de receitas (todos os períodos).
     */
    public BigDecimal totalReceitas() {
        return executarLeitura(em -> {
            BigDecimal resultado = em.createQuery(
                            "SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t " +
                            "WHERE t.tipo = :tipo",
                            BigDecimal.class)
                    .setParameter("tipo", TipoTransacao.RECEITA)
                    .getSingleResult();
            return resultado != null ? resultado : BigDecimal.ZERO;
        });
    }

    /**
     * Soma total de despesas (todos os períodos).
     */
    public BigDecimal totalDespesas() {
        return executarLeitura(em -> {
            BigDecimal resultado = em.createQuery(
                            "SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t " +
                            "WHERE t.tipo = :tipo",
                            BigDecimal.class)
                    .setParameter("tipo", TipoTransacao.DESPESA)
                    .getSingleResult();
            return resultado != null ? resultado : BigDecimal.ZERO;
        });
    }
}