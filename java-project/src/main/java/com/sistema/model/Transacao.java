package com.sistema.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade que representa uma transação financeira (receita ou despesa).
 */
@Entity
@Table(name = "transacoes")
public class Transacao {

    /**
     * Identificador unico da transacao.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "integer")
    private Long id;

    /**
     * Descricao textual da movimentacao financeira.
     */
    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    /**
     * Valor monetario da transacao.
     */
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    /**
     * Data em que a transacao ocorreu.
     */
    @Column(name = "data", nullable = false)
    private LocalDate data;

    /**
     * Tipo da transacao: receita ou despesa.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoTransacao tipo;

    /**
     * Categoria vinculada a transacao.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = true, columnDefinition = "integer")
    private Categoria categoria;

    // ── Construtores ──────────────────────────────────────────────────────────

    /**
     * Construtor padrao exigido pelo JPA.
     */
    public Transacao() {}

    /**
     * Cria uma transacao financeira completa.
     *
     * @param descricao descricao da transacao
     * @param valor valor monetario
     * @param data data da transacao
     * @param tipo tipo financeiro
     * @param categoria categoria vinculada
     */
    public Transacao(String descricao, BigDecimal valor, LocalDate data,
                     TipoTransacao tipo, Categoria categoria) {
        this.descricao = descricao;
        this.valor     = valor;
        this.data      = data;
        this.tipo      = tipo;
        this.categoria = categoria;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    /**
     * Retorna o identificador da transacao.
     *
     * @return identificador da transacao
     */
    public Long getId() { return id; }

    /**
     * Define o identificador da transacao.
     *
     * @param id identificador da transacao
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna a descricao da transacao.
     *
     * @return descricao da transacao
     */
    public String getDescricao() { return descricao; }

    /**
     * Define a descricao da transacao.
     *
     * @param descricao descricao da transacao
     */
    public void setDescricao(String descricao) { this.descricao = descricao; }

    /**
     * Retorna o valor da transacao.
     *
     * @return valor monetario
     */
    public BigDecimal getValor() { return valor; }

    /**
     * Define o valor da transacao.
     *
     * @param valor valor monetario
     */
    public void setValor(BigDecimal valor) { this.valor = valor; }

    /**
     * Retorna a data da transacao.
     *
     * @return data da transacao
     */
    public LocalDate getData() { return data; }

    /**
     * Define a data da transacao.
     *
     * @param data data da transacao
     */
    public void setData(LocalDate data) { this.data = data; }

    /**
     * Retorna o tipo da transacao.
     *
     * @return tipo financeiro
     */
    public TipoTransacao getTipo() { return tipo; }

    /**
     * Define o tipo da transacao.
     *
     * @param tipo tipo financeiro
     */
    public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }

    /**
     * Retorna a categoria vinculada.
     *
     * @return categoria da transacao
     */
    public Categoria getCategoria() { return categoria; }

    /**
     * Define a categoria vinculada.
     *
     * @param categoria categoria da transacao
     */
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    // ── equals / hashCode / toString ──────────────────────────────────────────

    /**
     * Compara transacoes pelo identificador.
     *
     * @param o objeto comparado
     * @return true quando representam a mesma transacao
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transacao t)) return false;
        return Objects.equals(id, t.id);
    }

    /**
     * Calcula o hash baseado no identificador.
     *
     * @return hash da transacao
     */
    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * Retorna uma representacao textual resumida.
     *
     * @return texto da transacao
     */
    @Override
    public String toString() {
        return "Transacao{id=%d, descricao='%s', valor=%s, tipo=%s}"
                .formatted(id, descricao, valor, tipo);
    }
}
