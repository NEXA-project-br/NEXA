package com.sistema.controller;

import com.sistema.dao.TransacaoDAO;
import com.sistema.model.TipoTransacao;
import com.sistema.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller de negócio para a entidade {@link Transacao}.
 * Centraliza cálculos financeiros, filtros e regras de negócio.
 */
public class TransacaoController implements GenericController<Transacao> {

    private final TransacaoDAO transacaoDAO;

    public TransacaoController() {
        this.transacaoDAO = new TransacaoDAO();
    }

    // ── Implementação GenericController ───────────────────────────────────────

    @Override
    public Transacao salvar(Transacao transacao) {
        validarTransacao(transacao);
        return transacaoDAO.salvar(transacao);
    }

    @Override
    public Transacao atualizar(Transacao transacao) {
        validarTransacao(transacao);
        if (transacao.getId() == null) {
            throw new IllegalArgumentException("ID da transação não pode ser nulo para atualização.");
        }
        return transacaoDAO.atualizar(transacao);
    }

    @Override
    public void excluir(Long id) {
        if (id == null) throw new IllegalArgumentException("ID não pode ser nulo.");
        transacaoDAO.excluir(id);
    }

    @Override
    public Optional<Transacao> buscarPorId(Long id) {
        return transacaoDAO.buscarPorId(id);
    }

    @Override
    public List<Transacao> listarTodos() {
        return transacaoDAO.listarOrdenadoPorData();
    }

    // ── Cálculos financeiros ──────────────────────────────────────────────────

    /**
     * Calcula o saldo atual: total de receitas menos total de despesas.
     *
     * @return saldo atual (pode ser negativo)
     */
    public BigDecimal calcularSaldoAtual() {
        BigDecimal receitas = transacaoDAO.totalReceitas();
        BigDecimal despesas = transacaoDAO.totalDespesas();
        return receitas.subtract(despesas);
    }

    /**
     * Retorna o total de receitas cadastradas.
     */
    public BigDecimal totalReceitas() {
        return transacaoDAO.totalReceitas();
    }

    /**
     * Retorna o total de despesas cadastradas.
     */
    public BigDecimal totalDespesas() {
        return transacaoDAO.totalDespesas();
    }

    /**
     * Calcula o saldo dentro de um período específico.
     *
     * @param inicio data inicial
     * @param fim    data final
     * @return saldo do período
     */
    public BigDecimal calcularSaldoPeriodo(LocalDate inicio, LocalDate fim) {
        BigDecimal receitas = transacaoDAO.somarPorTipoEPeriodo(TipoTransacao.RECEITA, inicio, fim);
        BigDecimal despesas = transacaoDAO.somarPorTipoEPeriodo(TipoTransacao.DESPESA, inicio, fim);
        return receitas.subtract(despesas);
    }

    // ── Filtros ───────────────────────────────────────────────────────────────

    /**
     * Lista as N transações mais recentes para exibição no dashboard.
     *
     * @param limite número máximo de transações
     */
    public List<Transacao> listarUltimas(int limite) {
        return transacaoDAO.listarUltimas(limite);
    }

    /**
     * Filtra transações por intervalo de datas.
     *
     * @param inicio data inicial (inclusive)
     * @param fim    data final (inclusive)
     */
    public List<Transacao> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return transacaoDAO.filtrarPorPeriodo(inicio, fim);
    }

    /**
     * Filtra transações por período e tipo.
     *
     * @param inicio data inicial
     * @param fim    data final
     * @param tipo   RECEITA ou DESPESA
     */
    public List<Transacao> filtrarPorPeriodoETipo(LocalDate inicio, LocalDate fim,
                                                   TipoTransacao tipo) {
        validarPeriodo(inicio, fim);
        return transacaoDAO.filtrarPorPeriodoETipo(inicio, fim, tipo);
    }

    // ── Dados para relatórios ─────────────────────────────────────────────────

    /**
     * Gera um resumo financeiro para um período específico.
     *
     * @param inicio data inicial
     * @param fim    data final
     * @return objeto ResumoFinanceiro com receitas, despesas e saldo
     */
    public ResumoFinanceiro gerarResumo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        BigDecimal receitas = transacaoDAO.somarPorTipoEPeriodo(TipoTransacao.RECEITA, inicio, fim);
        BigDecimal despesas = transacaoDAO.somarPorTipoEPeriodo(TipoTransacao.DESPESA, inicio, fim);
        BigDecimal saldo    = receitas.subtract(despesas);
        List<Transacao> transacoes = transacaoDAO.filtrarPorPeriodo(inicio, fim);
        return new ResumoFinanceiro(inicio, fim, receitas, despesas, saldo, transacoes);
    }

    // ── Validações ────────────────────────────────────────────────────────────

    private void validarTransacao(Transacao t) {
        if (t == null) {
            throw new IllegalArgumentException("Transação não pode ser nula.");
        }
        if (t.getDescricao() == null || t.getDescricao().isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        if (t.getValor() == null || t.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
        if (t.getData() == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        if (t.getTipo() == null) {
            throw new IllegalArgumentException("O tipo (RECEITA/DESPESA) é obrigatório.");
        }
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Período inválido: datas não podem ser nulas.");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }
    }

    // ── Record de resumo financeiro ───────────────────────────────────────────

    /**
     * Encapsula os dados de um relatório financeiro de período.
     */
    public record ResumoFinanceiro(
            LocalDate inicio,
            LocalDate fim,
            BigDecimal totalReceitas,
            BigDecimal totalDespesas,
            BigDecimal saldo,
            List<Transacao> transacoes
    ) {}
}