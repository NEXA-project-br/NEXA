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

/**
 * Janela para simulacao de renda por retirada mensal.
 */
public class IncomeCalculatorView extends JDialog {

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
    private static final Color COR_INPUT = new Color(255, 255, 255);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_RESULTADO = new Color(241, 245, 249);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_BTN = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField txtCapitalInicial;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField txtTaxaJuros;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField txtRetiradaMensal;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel lblTempo;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel lblSaldoFinal;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private CalculatorChartPanel chartPanel;

    /**
     * Cria uma nova instancia de IncomeCalculatorView.
     *
     * @param owner janela proprietaria do dialogo
     */
    public IncomeCalculatorView(Frame owner) {
        super(owner, "Calculadora de Renda", true);
        AppIconUtil.aplicar(this);
        construirInterface();
    }

    /**
     * Monta os componentes visuais da tela.
     */
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel titulo = new JLabel("Calculadora de Renda");
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
        JPanel painel = new JPanel(new BorderLayout(18, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(24, 24, 12, 24));
        painel.add(criarFormulario(), BorderLayout.WEST);

        chartPanel = new CalculatorChartPanel();
        painel.add(chartPanel, BorderLayout.CENTER);
        return painel;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setPreferredSize(new Dimension(300, 0));
        painel.setBackground(COR_FUNDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        adicionarLabel(painel, gbc, "Capital inicial (R$)");
        txtCapitalInicial = criarTextField("Ex.: 250000,00");
        MoneyDocumentFilter.aplicar(txtCapitalInicial);
        painel.add(txtCapitalInicial, gbc);

        adicionarLabel(painel, gbc, "Taxa de juros mensal (%)");
        txtTaxaJuros = criarTextField("Ex.: 0,80");
        MoneyDocumentFilter.aplicar(txtTaxaJuros);
        painel.add(txtTaxaJuros, gbc);

        adicionarLabel(painel, gbc, "Retirada mensal (R$)");
        txtRetiradaMensal = criarTextField("Ex.: 3000,00");
        MoneyDocumentFilter.aplicar(txtRetiradaMensal);
        painel.add(txtRetiradaMensal, gbc);

        adicionarLabel(painel, gbc, "Tempo ate acabar");
        lblTempo = criarLabelResultado("-");
        painel.add(lblTempo, gbc);

        adicionarLabel(painel, gbc, "Saldo final");
        lblSaldoFinal = criarLabelResultado("R$ 0,00");
        painel.add(lblSaldoFinal, gbc);
        return painel;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarRodape() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 12, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 24, 24, 24));

        JButton btnFechar = criarBotao("Fechar", COR_GRAFITE);
        JButton btnLimpar = criarBotao("Limpar", COR_GRAFITE);
        JButton btnCalcular = criarBotao("Calcular", COR_AZUL);

        btnFechar.addActionListener(e -> dispose());
        btnLimpar.addActionListener(e -> limparCampos());
        btnCalcular.addActionListener(e -> calcularRenda());

        painel.add(btnFechar);
        painel.add(btnLimpar);
        painel.add(btnCalcular);
        return painel;
    }

    /**
     * Calcula os valores financeiros informados na tela.
     */
    private void calcularRenda() {
        try {
            BigDecimal saldo = parseMoeda(txtCapitalInicial.getText());
            BigDecimal taxaMensal = parsePercentual(txtTaxaJuros.getText());
            BigDecimal retirada = parseMoeda(txtRetiradaMensal.getText());

            if (saldo.compareTo(BigDecimal.ZERO) <= 0
                    || taxaMensal.compareTo(BigDecimal.ZERO) < 0
                    || retirada.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Informe capital e retirada maiores que zero e taxa valida.");
            }

            if (saldo.compareTo(retirada) < 0) {
                throw new IllegalArgumentException("Nao e possivel realizar esta operacao.");
            }

            List<BigDecimal> evolucao = new ArrayList<>();
            evolucao.add(saldo);
            BigDecimal fator = BigDecimal.ONE.add(taxaMensal);
            MathContext mc = new MathContext(12, RoundingMode.HALF_UP);
            BigDecimal rendimentoMensal = saldo.multiply(taxaMensal, mc);
            if (rendimentoMensal.compareTo(retirada) >= 0) {
                for (int i = 0; i < 120; i++) {
                    saldo = saldo.multiply(fator, mc).subtract(retirada, mc);
                    evolucao.add(saldo);
                }
                lblTempo.setText("O patrimônio nunca se esgota");
                lblSaldoFinal.setText(CurrencyUtil.formatar(saldo));
                chartPanel.atualizarDados("Valor restante ao longo do tempo", evolucao);
                return;
            }
            int meses = 0;
            int limiteMeses = 1200;

            while (saldo.compareTo(BigDecimal.ZERO) > 0 && meses < limiteMeses) {
                saldo = saldo.multiply(fator, mc).subtract(retirada, mc);
                meses++;
                evolucao.add(saldo.max(BigDecimal.ZERO));
            }

            if (meses >= limiteMeses && saldo.compareTo(BigDecimal.ZERO) > 0) {
                lblTempo.setText("Mais de 100 anos");
            } else {
                lblTempo.setText(formatarTempo(meses));
            }
            lblSaldoFinal.setText(CurrencyUtil.formatar(saldo.max(BigDecimal.ZERO)));
            chartPanel.atualizarDados("Valor restante ao longo do tempo", evolucao);
        } catch (NumberFormatException e) {
            mostrarErro("Preencha os valores numericos corretamente.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Nao foi possivel calcular: " + e.getMessage());
        }
    }

    /**
     * Limpa os campos e resultados exibidos na tela.
     */
    private void limparCampos() {
        txtCapitalInicial.setText("");
        txtTaxaJuros.setText("");
        txtRetiradaMensal.setText("");
        lblTempo.setText("-");
        lblSaldoFinal.setText("R$ 0,00");
        chartPanel.limpar();
    }

    /**
     * Converte o texto informado para valor numerico.
     *
     * @param valor parametro valor
     * @return valor monetario calculado
     */
    private BigDecimal parseMoeda(String valor) {
        String normalizado = valor.trim().replace("R$", "").replace(" ", "").replace(".", "").replace(",", ".");
        if (normalizado.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalizado);
    }

    /**
     * Converte o texto informado para valor numerico.
     *
     * @param valor parametro valor
     * @return valor monetario calculado
     */
    private BigDecimal parsePercentual(String valor) {
        return parseMoeda(valor).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
    }

    /**
     * Formata o valor informado para exibicao.
     *
     * @param meses parametro meses
     * @return texto formatado
     */
    private String formatarTempo(int meses) {
        int anos = meses / 12;
        int mesesRestantes = meses % 12;
        if (anos == 0) {
            return meses + (meses == 1 ? " mes" : " meses");
        }
        return anos + (anos == 1 ? " ano" : " anos") + " e "
                + mesesRestantes + (mesesRestantes == 1 ? " mes" : " meses");
    }

    /**
     * Adiciona o componente configurado ao painel informado.
     *
     * @param painel parametro painel
     * @param gbc parametro gbc
     * @param texto parametro texto
     */
    private void adicionarLabel(JPanel painel, GridBagConstraints gbc, String texto) {
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_MUTED);
        painel.add(label, gbc);
        gbc.insets = new Insets(0, 0, 14, 0);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param toolTip parametro toolTip
     * @return campo de texto configurado
     */
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @return rotulo configurado
     */
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param cor parametro cor
     * @return botao configurado
     */
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

    /**
     * Exibe uma mensagem para o usuario.
     *
     * @param mensagem parametro mensagem
     */
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
