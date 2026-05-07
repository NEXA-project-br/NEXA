package com.sistema.view;

import com.sistema.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CompoundInterestCalculatorView extends JDialog {

    private static final Color COR_FUNDO = new Color(248, 250, 252);
    private static final Color COR_CARD = new Color(255, 255, 255);
    private static final Color COR_BORDA = new Color(203, 213, 225);
    private static final Color COR_AZUL = new Color(59, 130, 246);
    private static final Color COR_GRAFITE = new Color(100, 116, 139);
    private static final Color COR_TEXTO = new Color(15, 23, 42);
    private static final Color COR_MUTED = new Color(71, 85, 105);
    private static final Color COR_INPUT = new Color(255, 255, 255);
    private static final Color COR_RESULTADO = new Color(241, 245, 249);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BTN = new Font("Segoe UI", Font.BOLD, 14);

    private JTextField txtValorInicial;
    private JTextField txtAporteMensal;
    private JTextField txtTaxaJuros;
    private JTextField txtPeriodo;
    private JComboBox<String> cmbUnidadePeriodo;
    private JLabel lblMontante;
    private JLabel lblJuros;
    private CalculatorChartPanel chartPanel;

    public CompoundInterestCalculatorView(Frame owner) {
        super(owner, "Calculadora de Juros Compostos", true);
        AppIconUtil.aplicar(this);
        construirInterface();
    }

    private void construirInterface() {
        setSize(900, 560);
        setMinimumSize(new Dimension(780, 500));
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpo(), BorderLayout.CENTER);
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

    private JPanel criarCorpo() {
        JPanel painel = new JPanel(new BorderLayout(18, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(24, 24, 12, 24));
        painel.add(criarFormularioScroll(), BorderLayout.WEST);

        chartPanel = new CalculatorChartPanel();
        painel.add(chartPanel, BorderLayout.CENTER);
        return painel;
    }

    private JScrollPane criarFormularioScroll() {
        JScrollPane scroll = new JScrollPane(criarFormulario());
        scroll.setPreferredSize(new Dimension(320, 0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(COR_FUNDO);
        scroll.getViewport().setBackground(COR_FUNDO);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 0, 0, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        adicionarLabel(painel, gbc, "Valor inicial (R$)");
        txtValorInicial = criarTextField("0,00");
        MoneyDocumentFilter.aplicar(txtValorInicial);
        painel.add(txtValorInicial, gbc);

        adicionarLabel(painel, gbc, "Aporte mensal (R$)");
        txtAporteMensal = criarTextField("0,00");
        MoneyDocumentFilter.aplicar(txtAporteMensal);
        painel.add(txtAporteMensal, gbc);

        adicionarLabel(painel, gbc, "Taxa de juros mensal (%)");
        txtTaxaJuros = criarTextField("1,00");
        MoneyDocumentFilter.aplicar(txtTaxaJuros);
        painel.add(txtTaxaJuros, gbc);

        adicionarLabel(painel, gbc, "Periodo");
        txtPeriodo = criarTextField("12");
        cmbUnidadePeriodo = new JComboBox<>(new String[]{"Meses", "Anos"});
        estilizarComboBox(cmbUnidadePeriodo);
        painel.add(criarLinhaPeriodo(), gbc);

        adicionarLabel(painel, gbc, "Montante final");
        lblMontante = criarLabelResultado("R$ 0,00");
        painel.add(lblMontante, gbc);

        adicionarLabel(painel, gbc, "Juros ganhos");
        lblJuros = criarLabelResultado("R$ 0,00");
        painel.add(lblJuros, gbc);

        return painel;
    }

    private JPanel criarRodape() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 12, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 24, 24, 24));

        JButton btnFechar = criarBotao("Fechar", COR_GRAFITE);
        JButton btnLimpar = criarBotao("Limpar", COR_GRAFITE);
        JButton btnCalcular = criarBotao("Calcular", COR_AZUL);

        btnFechar.addActionListener(e -> dispose());
        btnLimpar.addActionListener(e -> limparCampos());
        btnCalcular.addActionListener(e -> calcularJuros());

        painel.add(btnFechar);
        painel.add(btnLimpar);
        painel.add(btnCalcular);
        return painel;
    }

    private void calcularJuros() {
        try {
            BigDecimal valorInicial = parseMoeda(txtValorInicial.getText());
            BigDecimal aporteMensal = parseMoeda(txtAporteMensal.getText());
            BigDecimal taxaMensal = parsePercentual(txtTaxaJuros.getText());
            int periodo = Integer.parseInt(txtPeriodo.getText().trim());
            int meses = converterPeriodoParaMeses(periodo);

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
            List<BigDecimal> evolucao = new ArrayList<>();
            evolucao.add(montante);

            for (int i = 0; i < meses; i++) {
                montante = montante.multiply(fator, mc).add(aporteMensal, mc);
                totalInvestido = totalInvestido.add(aporteMensal);
                evolucao.add(montante);
            }

            BigDecimal juros = montante.subtract(totalInvestido).max(BigDecimal.ZERO);
            lblMontante.setText(CurrencyUtil.formatar(montante));
            lblJuros.setText(CurrencyUtil.formatar(juros));
            chartPanel.atualizarDados("Evolucao do montante", evolucao);
        } catch (NumberFormatException e) {
            mostrarErro("Preencha o periodo em meses com um numero inteiro.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Nao foi possivel calcular: " + e.getMessage());
        }
    }

    private void limparCampos() {
        txtValorInicial.setText("");
        txtAporteMensal.setText("");
        txtTaxaJuros.setText("");
        txtPeriodo.setText("");
        cmbUnidadePeriodo.setSelectedItem("Meses");
        lblMontante.setText("R$ 0,00");
        lblJuros.setText("R$ 0,00");
        chartPanel.limpar();
    }

    private int converterPeriodoParaMeses(int periodo) {
        if ("Anos".equals(cmbUnidadePeriodo.getSelectedItem())) {
            return Math.multiplyExact(periodo, 12);
        }
        return periodo;
    }

    private BigDecimal parseMoeda(String valor) {
        String normalizado = valor.trim().replace("R$", "").replace(" ", "").replace(".", "").replace(",", ".");
        if (normalizado.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalizado);
    }

    private BigDecimal parsePercentual(String valor) {
        return parseMoeda(valor).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
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

    private JPanel criarLinhaPeriodo() {
        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.setBackground(COR_FUNDO);
        linha.add(txtPeriodo, BorderLayout.CENTER);
        linha.add(cmbUnidadePeriodo, BorderLayout.EAST);
        return linha;
    }

    private <E> void estilizarComboBox(JComboBox<E> combo) {
        combo.setFont(FONTE_INPUT);
        combo.setForeground(COR_TEXTO);
        combo.setBackground(COR_INPUT);
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        combo.setPreferredSize(new Dimension(96, 36));
    }

    private JLabel criarLabelResultado(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_INPUT.deriveFont(Font.BOLD));
        label.setForeground(COR_AZUL);
        label.setOpaque(true);
        label.setBackground(COR_RESULTADO);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, COR_BORDA),
                new EmptyBorder(10, 12, 10, 12)));
        return label;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_BTN);
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
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
