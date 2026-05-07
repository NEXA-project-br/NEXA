package com.sistema.model;

/**
 * Enumeração para classificar o tipo de transação financeira.
 */
public enum TipoTransacao {

    /**
     * Entrada de dinheiro.
     */
    RECEITA("Receita"),

    /**
     * Saida de dinheiro.
     */
    DESPESA("Despesa");

    /**
     * Descricao exibida para o usuario.
     */
    private final String descricao;

    /**
     * Cria o tipo com a descricao de exibicao.
     *
     * @param descricao descricao amigavel
     */
    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descricao amigavel do tipo.
     *
     * @return descricao do tipo
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a descricao para exibicao em combos e tabelas.
     *
     * @return descricao do tipo
     */
    @Override
    public String toString() {
        return descricao;
    }
}
