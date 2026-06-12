package com.sistema.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Janela de menu que centraliza o acesso as calculadoras financeiras.
 */
public class CalculatorMenuView extends JDialog {

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_FUNDO = new Color(248, 250, 252);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_CARD = new Color(255, 255, 255);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_BORDA = new Color(203, 213, 225);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_AZUL = new Color(59, 130, 246);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_VERDE = new Color(34, 197, 94);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_GRAFITE = new Color(100, 116, 139);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_TEXTO = new Color(15, 23, 42);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_MUTED = new Color(71, 85, 105);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_CARD_TITULO = new Font("Segoe UI", Font.BOLD, 15);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_CARD_DESC = new Font("Segoe UI", Font.PLAIN, 12);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_BTN = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Cria uma nova instancia de CalculatorMenuView.
     *
     * @param owner janela proprietaria do dialogo
     */
    public CalculatorMenuView(Frame owner) {
        super(owner, "Calculadoras", true);
        AppIconUtil.aplicar(this);
        construirInterface();
    }

    /**
     * Monta os componentes visuais da tela.
     */
    private void construirInterface() {
        setSize(780, 360);
        setMinimumSize(new Dimension(680, 320));
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpo(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel titulo = new JLabel("Calculadoras");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO);
        painel.add(titulo, BorderLayout.WEST);
        return painel;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarCorpo() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 16, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(24, 24, 18, 24));

        painel.add(criarCard(
                "Juros Compostos",
                "Projete aportes mensais e veja o crescimento do patrimônio.",
                COR_AZUL,
                () -> abrirCalculadora(new CompoundInterestCalculatorView((Frame) getOwner()))));
        painel.add(criarCard(
                "Calculadora de Renda",
                "Estime por quanto tempo seu patrimônio pode sustentar retiradas mensais.",
                COR_GRAFITE,
                () -> abrirCalculadora(new IncomeCalculatorView((Frame) getOwner()))));
        painel.add(criarCard(
                "Primeiro Milhão",
                "Descubra quanto tempo falta para chegar a R$ 1.000.000.",
                COR_VERDE,
                () -> abrirCalculadora(new FirstMillionCalculatorView((Frame) getOwner()))));

        return painel;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param titulo parametro titulo
     * @param descricao parametro descricao
     * @param cor parametro cor
     * @param acao parametro acao
     * @return painel configurado
     */
    private JPanel criarCard(String titulo, String descricao, Color cor, Runnable acao) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel icone = new JLabel(UiIcons.calculator(cor));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_CARD_TITULO);
        lblTitulo.setForeground(COR_TEXTO);

        JTextArea txtDescricao = new JTextArea(descricao);
        txtDescricao.setFont(FONTE_CARD_DESC);
        txtDescricao.setForeground(COR_MUTED);
        txtDescricao.setBackground(COR_CARD);
        txtDescricao.setEditable(false);
        txtDescricao.setFocusable(false);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JPanel topo = new JPanel(new BorderLayout(8, 0));
        topo.setOpaque(false);
        topo.add(icone, BorderLayout.WEST);
        topo.add(lblTitulo, BorderLayout.CENTER);

        JButton btnAbrir = criarBotao("Abrir", cor, UiIcons.chart(Color.WHITE));
        btnAbrir.addActionListener(e -> acao.run());

        card.add(topo, BorderLayout.NORTH);
        card.add(txtDescricao, BorderLayout.CENTER);
        card.add(btnAbrir, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarRodape() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 24, 20, 24));

        JButton btnFechar = criarBotao("Fechar", COR_GRAFITE, UiIcons.close(Color.WHITE));
        btnFechar.addActionListener(e -> dispose());
        painel.add(btnFechar);
        return painel;
    }

    /**
     * Abre a janela informada para o usuario.
     *
     * @param calculadora parametro calculadora
     */
    private void abrirCalculadora(JDialog calculadora) {
        calculadora.setVisible(true);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param cor parametro cor
     * @param icone parametro icone
     * @return botao configurado
     */
    private JButton criarBotao(String texto, Color cor, Icon icone) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setIcon(icone);
        btn.setIconTextGap(8);
        return btn;
    }
}
