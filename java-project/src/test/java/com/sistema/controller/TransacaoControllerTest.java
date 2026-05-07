package com.sistema.controller;

import com.sistema.model.TipoTransacao;
import com.sistema.model.Transacao;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertThrows;

/**
 * Testes unitarios das validacoes de negocio de {@link TransacaoController}.
 */
public class TransacaoControllerTest {

    /**
     * Controller testado em cada cenario.
     */
    private TransacaoController controller;

    /**
     * Inicializa uma nova instancia do controller antes de cada teste.
     */
    @Before
    public void setUp() {
        controller = new TransacaoController();
    }

    /**
     * Deve rejeitar uma transacao nula antes de acessar a camada DAO.
     */
    @Test
    public void salvarDeveRejeitarTransacaoNula() {
        assertThrows(IllegalArgumentException.class, () -> controller.salvar(null));
    }

    /**
     * Deve rejeitar transacoes sem descricao preenchida.
     */
    @Test
    public void salvarDeveRejeitarDescricaoEmBranco() {
        Transacao transacao = transacaoValida();
        transacao.setDescricao(" ");

        assertThrows(IllegalArgumentException.class, () -> controller.salvar(transacao));
    }

    /**
     * Deve rejeitar valores nulos, zerados ou negativos.
     */
    @Test
    public void salvarDeveRejeitarValorInvalido() {
        Transacao transacao = transacaoValida();
        transacao.setValor(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> controller.salvar(transacao));
    }

    /**
     * Deve rejeitar transacoes sem data.
     */
    @Test
    public void salvarDeveRejeitarDataNula() {
        Transacao transacao = transacaoValida();
        transacao.setData(null);

        assertThrows(IllegalArgumentException.class, () -> controller.salvar(transacao));
    }

    /**
     * Deve rejeitar transacoes sem tipo.
     */
    @Test
    public void salvarDeveRejeitarTipoNulo() {
        Transacao transacao = transacaoValida();
        transacao.setTipo(null);

        assertThrows(IllegalArgumentException.class, () -> controller.salvar(transacao));
    }

    /**
     * Deve exigir ID para atualizar uma transacao existente.
     */
    @Test
    public void atualizarDeveRejeitarIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> controller.atualizar(transacaoValida()));
    }

    /**
     * Deve exigir ID para excluir uma transacao.
     */
    @Test
    public void excluirDeveRejeitarIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> controller.excluir(null));
    }

    /**
     * Deve rejeitar periodos com data inicial posterior a data final.
     */
    @Test
    public void filtrarPorPeriodoDeveRejeitarPeriodoInvertido() {
        LocalDate inicio = LocalDate.of(2026, 5, 2);
        LocalDate fim = LocalDate.of(2026, 5, 1);

        assertThrows(IllegalArgumentException.class, () -> controller.filtrarPorPeriodo(inicio, fim));
    }

    /**
     * Cria uma transacao valida para reutilizacao nos testes.
     *
     * @return transacao valida sem ID persistido
     */
    private Transacao transacaoValida() {
        return new Transacao(
                "Salario",
                new BigDecimal("1000.00"),
                LocalDate.of(2026, 5, 1),
                TipoTransacao.RECEITA,
                null
        );
    }
}
