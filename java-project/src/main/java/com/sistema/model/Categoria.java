package com.sistema.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categorias")
public class Categoria {

    /**
     * Identificador unico da categoria.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "integer")
    private Long id;

    /**
     * Nome exibido para a categoria.
     */
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    /**
     * Tipo financeiro associado a categoria.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = true, length = 10)
    private TipoTransacao tipo;

    /**
     * Transacoes vinculadas a categoria.
     */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Transacao> transacoes = new ArrayList<>();

    /**
     * Construtor padrao exigido pelo JPA.
     */
    public Categoria() {}

    /**
     * Cria uma categoria com nome e tipo financeiro.
     *
     * @param nome nome da categoria
     * @param tipo tipo da categoria
     */
    public Categoria(String nome, TipoTransacao tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    /**
     * Retorna o identificador da categoria.
     *
     * @return identificador da categoria
     */
    public Long getId()                          { return id; }

    /**
     * Define o identificador da categoria.
     *
     * @param id identificador da categoria
     */
    public void setId(Long id)                   { this.id = id; }

    /**
     * Retorna o nome da categoria.
     *
     * @return nome da categoria
     */
    public String getNome()                      { return nome; }

    /**
     * Define o nome da categoria.
     *
     * @param nome nome da categoria
     */
    public void setNome(String nome)             { this.nome = nome; }

    /**
     * Retorna o tipo financeiro da categoria.
     *
     * @return tipo da categoria
     */
    public TipoTransacao getTipo()               { return tipo; }

    /**
     * Define o tipo financeiro da categoria.
     *
     * @param tipo tipo da categoria
     */
    public void setTipo(TipoTransacao tipo)      { this.tipo = tipo; }

    /**
     * Retorna as transacoes vinculadas.
     *
     * @return lista de transacoes
     */
    public List<Transacao> getTransacoes()       { return transacoes; }

    /**
     * Define as transacoes vinculadas.
     *
     * @param t lista de transacoes
     */
    public void setTransacoes(List<Transacao> t) { this.transacoes = t; }

    /**
     * Compara categorias pelo identificador.
     *
     * @param o objeto comparado
     * @return true quando representam a mesma categoria
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria c)) return false;
        return Objects.equals(id, c.id);
    }

    /**
     * Calcula o hash baseado no identificador.
     *
     * @return hash da categoria
     */
    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * Retorna o nome para exibicao em componentes Swing.
     *
     * @return nome da categoria
     */
    @Override
    public String toString() { return nome; }
}
