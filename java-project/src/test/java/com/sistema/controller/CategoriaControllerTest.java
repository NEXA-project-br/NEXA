package com.sistema.controller;

import com.sistema.model.Categoria;
import com.sistema.model.TipoTransacao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

/**
 * Testes unitarios das validacoes de negocio de {@link CategoriaController}.
 */
public class CategoriaControllerTest {

    /**
     * Controller testado em cada cenario.
     */
    private CategoriaController controller;

    /**
     * Inicializa uma nova instancia do controller antes de cada teste.
     */
    @Before
    public void setUp() {
        controller = new CategoriaController();
    }

    /**
     * Deve rejeitar uma categoria nula antes de acessar a camada DAO.
     */
    @Test
    public void salvarDeveRejeitarCategoriaNula() {
        assertThrows(IllegalArgumentException.class, () -> controller.salvar(null));
    }

    /**
     * Deve rejeitar categorias sem nome preenchido.
     */
    @Test
    public void salvarDeveRejeitarNomeEmBranco() {
        Categoria categoria = new Categoria("   ", TipoTransacao.DESPESA);

        assertThrows(IllegalArgumentException.class, () -> controller.salvar(categoria));
    }

    /**
     * Deve rejeitar categorias sem tipo de transacao.
     */
    @Test
    public void salvarDeveRejeitarTipoNulo() {
        Categoria categoria = new Categoria("Mercado", null);

        assertThrows(IllegalArgumentException.class, () -> controller.salvar(categoria));
    }

    /**
     * Deve exigir ID para atualizar uma categoria existente.
     */
    @Test
    public void atualizarDeveRejeitarIdNulo() {
        Categoria categoria = new Categoria("Transporte", TipoTransacao.DESPESA);

        assertThrows(IllegalArgumentException.class, () -> controller.atualizar(categoria));
    }

    /**
     * Deve exigir ID para excluir uma categoria.
     */
    @Test
    public void excluirDeveRejeitarIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> controller.excluir(null));
    }
}
