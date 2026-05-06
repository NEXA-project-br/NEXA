package com.sistema.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CompoundInterestCalculatorView extends JDialog {

    private static final Color COR_FUNDO       = new Color(248, 250, 252);
    private static final Color COR_CARD        = new Color(255, 255, 255);
    private static final Color COR_BORDA       = new Color(203, 213, 225);
    private static final Color COR_AZUL        = new Color(59, 130, 246);
    private static final Color COR_TEXTO       = new Color(15, 23, 42);
    private static final Color COR_MUTED       = new Color(71, 85, 105);
    private static final Color COR_INPUT       = new Color(255, 255, 255);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_INPUT  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BTN    = new Font("Segoe UI", Font.BOLD, 14);

    private JTextField txtValorInicial;
    private JTextField txtAporteMensal;
    private JTextField txtTaxaJuros;
    private JTextField txtPeriodo;
    private JLabel lblMontante;
    private JLabel lblJuros;

    public CompoundInterestCalculatorView(Frame owner) {
        super(owner, "Calculadora de Juros Compostos", true);
        construirInterface();
    }

    private void construirInterface() {
        setSize(500, 430);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarFormulario(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel titulo = new JLabel("Calculadora de Juros Compostos");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO);
        painel.add(titulo, BorderLayout.WEST);
        return painel;
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(24, 24, 12, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        adicionarLabel(painel, gbc, "Valor inicial (R$)");
        txtValorInicial = criarTextField("0,00");
        painel.add(txtValorInicial, gbc);

        adicionarLabel(painel, gbc, "Aporte mensal (R$)");
        txtAporteMensal = criarTextField("0,00");
        painel.add(txtAporteMensal, gbc);

        adicionarLabel(painel, gbc, "Taxa de juros mensal (%)");
        txtTaxaJuros = criarTextField("1,00");
        painel.add(txtTaxaJuros, gbc);

        adicionarLabel(painel, gbc, "Periodo (meses)");
        txtPeriodo = criarTextField("12");
        painel.add(txtPeriodo, gbc);

        adicionarLabel(painel, gbc, "Montante final");
        lblMontante = criarLabelResultado("R$ 0,00");
        painel.add(lblMontante, gbc);

        adicionarLabel(painel, gbc, "Juros ganhos");
        lblJuros = criarLabelResultado("R$ 0,00");
        painel.add(lblJuros, gbc);

        return painel;
    }

    private JPanel criarRodape() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 12, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 24, 24, 24));

        JButton btnFechar = criarBotao("Fechar");
        JButton btnCalcular = criarBotao("Calcular");

        btnFechar.addActionListener(e -> dispose());
        btnCalcular.addActionListener(e -> calcularJuros());

        painel.add(btnFechar);
        painel.add(btnCalcular);
        return painel;
    }

    private void calcularJuros() {
        try {
            BigDecimal valorInicial = parseMoeda(txtValorInicial.getText());
            BigDecimal aporteMensal = parseMoeda(txtAporteMensal.getText());
            BigDecimal taxaMensal = parsePercentual(txtTaxaJuros.getText());
            int meses = Integer.parseInt(txtPeriodo.getText().trim());

            if (valorInicial.compareTo(BigDecimal.ZERO) < 0
                    || aporteMensal.compareTo(BigDecimal.ZERO) < 0
                    || taxaMensal.compareTo(BigDecimal.ZERO) < 0
                    || meses <= 0) {
                throw new IllegalArgumentException("Informe valores validos e um periodo maior que zero.");
            }

            BigDecimal montante = valorInicial;
            BigDecimal totalInvestido = valorInicial;
            BigDecimal fator = BigDecimal.ONE.add(taxaMensal);
            MathContext mc = new MathContext(12, RoundingMode.HALF_UP);

            for (int i = 0; i < meses; i++) {
                montante = montante.multiply(fator, mc).add(aporteMensal, mc);
                totalInvestido = totalInvestido.add(aporteMensal);
            }

            BigDecimal juros = montante.subtract(totalInvestido).max(BigDecimal.ZERO);
            lblMontante.setText(formatarMoeda(montante));
            lblJuros.setText(formatarMoeda(juros));
        } catch (NumberFormatException e) {
            mostrarErro("Preencha o periodo em meses com um numero inteiro.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Nao foi possivel calcular: " + e.getMessage());
        }
    }

    private BigDecimal parseMoeda(String valor) {
        String normalizado = valor.trim().replace(".", "").replace(",", ".");
        if (normalizado.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalizado);
    }

    private BigDecimal parsePercentual(String valor) {
        return parseMoeda(valor).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
    }

    private String formatarMoeda(BigDecimal valor) {
        BigDecimal valorFormatado = valor.setScale(2, RoundingMode.HALF_UP);
        String texto = valorFormatado.toPlainString().replace(".", ",");
        return "R$ " + texto;
    }

    private void adicionarLabel(JPanel painel, GridBagConstraints gbc, String texto) {
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_MUTED);
        painel.add(label, gbc);
        gbc.insets = new Insets(0, 0, 14, 0);
    }

    private JTextField criarTextField(String toolTip) {
        JTextField field = new JTextField();
        field.setFont(FONTE_INPUT);
        field.setForeground(COR_TEXTO);
        field.setBackground(COR_INPUT);
        field.setCaretColor(COR_TEXTO);
        field.setToolTipText(toolTip);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(8, 12, 8, 12)));
        return field;
    }

    private JLabel criarLabelResultado(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_INPUT);
        label.setForeground(COR_TEXTO);
        label.setOpaque(true);
        label.setBackground(COR_INPUT);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(10, 12, 10, 12)));
        return label;
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_BTN);
        botao.setForeground(Color.WHITE);
        botao.setBackground(COR_AZUL);
        botao.setBorder(new EmptyBorder(10, 0, 10, 0));
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        return botao;
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
