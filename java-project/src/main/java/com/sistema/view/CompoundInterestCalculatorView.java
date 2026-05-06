package com.sistema.view;

import com.sistema.util.AppIcon;
import com.sistema.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CompoundInterestCalculatorView extends JDialog {

    private static final Color COR_FUNDO       = new Color(248, 250, 252);
    private static final Color COR_CARD        = new Color(255, 255, 255);
    private static final Color COR_BORDA       = new Color(203, 213, 225);
    private static final Color COR_AZUL        = new Color(59, 130, 246);
    private static final Color COR_TEXTO       = new Color(15, 23, 42);
    private static final Color COR_MUTED       = new Color(71, 85, 105);
    private static final Color COR_INPUT       = new Color(255, 255, 255);
    private static final Color COR_VERDE       = new Color(34, 197, 94);
    private static final Color COR_LARANJA     = new Color(245, 158, 11);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_INPUT  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BTN    = new Font("Segoe UI", Font.BOLD, 14);
    private static final NumberFormat FMT_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private JTextField txtValorInicial;
    private JTextField txtAporteMensal;
    private JTextField txtTaxaJuros;
    private JTextField txtPeriodo;
    private JLabel lblMontante;
    private JLabel lblJuros;

    public CompoundInterestCalculatorView(Frame owner) {
        super(owner, "Calculadora de Juros Compostos", true);
        AppIcon.aplicar(this);
        construirInterface();
    }

    private void construirInterface() {
        setSize(500, 560);
        setMinimumSize(new Dimension(500, 560));
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
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 24, 10, 24));

        txtValorInicial = criarTextField("0,00");
        painel.add(criarLinhaCampo("Valor inicial (R$)", txtValorInicial));

        txtAporteMensal = criarTextField("0,00");
        painel.add(criarLinhaCampo("Aporte mensal (R$)", txtAporteMensal));

        txtTaxaJuros = criarTextField("1,00");
        painel.add(criarLinhaCampo("Taxa de juros mensal (%)", txtTaxaJuros));

        txtPeriodo = criarTextField("12");
        painel.add(criarLinhaCampo("Periodo (meses)", txtPeriodo));

        lblMontante = criarLabelResultado("R$ 0,00");
        painel.add(criarLinhaCampo("Montante final", lblMontante));

        lblJuros = criarLabelResultado("R$ 0,00");
        painel.add(criarLinhaCampo("Juros acumulados", lblJuros));

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

            List<ResultadoMes> resultados = calcularEvolucao(valorInicial, aporteMensal, taxaMensal, meses);
            ResultadoMes ultimoMes = resultados.get(resultados.size() - 1);

            lblMontante.setText(formatarMoeda(ultimoMes.montante()));
            lblJuros.setText(formatarMoeda(ultimoMes.juros()));
            perguntarSeDesejaVerGrafico(resultados);
        } catch (NumberFormatException e) {
            mostrarErro("Preencha o periodo em meses com um numero inteiro.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Nao foi possivel calcular: " + e.getMessage());
        }
    }

    private List<ResultadoMes> calcularEvolucao(BigDecimal valorInicial, BigDecimal aporteMensal,
                                                 BigDecimal taxaMensal, int meses) {
        List<ResultadoMes> resultados = new ArrayList<>();
        BigDecimal montante = valorInicial;
        BigDecimal totalInvestido = valorInicial;
        BigDecimal fator = BigDecimal.ONE.add(taxaMensal);
        MathContext mc = new MathContext(12, RoundingMode.HALF_UP);

        for (int mes = 1; mes <= meses; mes++) {
            montante = montante.multiply(fator, mc).add(aporteMensal, mc);
            totalInvestido = totalInvestido.add(aporteMensal);
            BigDecimal juros = montante.subtract(totalInvestido).max(BigDecimal.ZERO);
            resultados.add(new ResultadoMes(mes, montante, totalInvestido, juros));
        }

        return resultados;
    }

    private void perguntarSeDesejaVerGrafico(List<ResultadoMes> resultados) {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Calculo concluido. Deseja ver o grafico da evolucao?",
                "Ver grafico",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            GraficoJurosDialog grafico = new GraficoJurosDialog(this, resultados);
            grafico.setVisible(true);
        }
    }

    private BigDecimal parseMoeda(String valor) {
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }
        return CurrencyUtil.parsear(valor);
    }

    private BigDecimal parsePercentual(String valor) {
        return parseMoeda(valor).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
    }

    private String formatarMoeda(BigDecimal valor) {
        return FMT_MOEDA.format(valor.setScale(2, RoundingMode.HALF_UP));
    }

    private JPanel criarLinhaCampo(String texto, JComponent campo) {
        JPanel linha = new JPanel();
        linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        linha.setPreferredSize(new Dimension(0, 58));

        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        campo.setPreferredSize(new Dimension(0, 34));

        linha.add(label);
        linha.add(Box.createVerticalStrut(4));
        linha.add(campo);
        linha.add(Box.createVerticalStrut(10));
        return linha;
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

    private record ResultadoMes(int mes, BigDecimal montante, BigDecimal totalInvestido, BigDecimal juros) {}

    private static class GraficoJurosDialog extends JDialog {

        private GraficoJurosDialog(Dialog owner, List<ResultadoMes> resultados) {
            super(owner, "Grafico de Juros Compostos", true);
            AppIcon.aplicar(this);
            setSize(760, 480);
            setMinimumSize(new Dimension(680, 420));
            setLocationRelativeTo(owner);
            getContentPane().setBackground(COR_FUNDO);
            setLayout(new BorderLayout());

            add(criarCabecalho(resultados), BorderLayout.NORTH);
            add(criarCorpo(resultados), BorderLayout.CENTER);
            add(criarRodape(), BorderLayout.SOUTH);
        }

        private JPanel criarCabecalho(List<ResultadoMes> resultados) {
            JPanel painel = new JPanel(new BorderLayout());
            painel.setBackground(COR_CARD);
            painel.setBorder(new EmptyBorder(18, 24, 14, 24));

            ResultadoMes ultimo = resultados.get(resultados.size() - 1);
            JLabel titulo = new JLabel("Evolucao do investimento");
            titulo.setFont(FONTE_TITULO);
            titulo.setForeground(COR_TEXTO);

            JLabel resumo = new JLabel("Montante: " + FMT_MOEDA.format(ultimo.montante()));
            resumo.setFont(FONTE_LABEL);
            resumo.setForeground(COR_MUTED);

            painel.add(titulo, BorderLayout.WEST);
            painel.add(resumo, BorderLayout.EAST);
            return painel;
        }

        private JPanel criarCorpo(List<ResultadoMes> resultados) {
            JPanel painel = new JPanel(new BorderLayout());
            painel.setBackground(COR_FUNDO);
            painel.setBorder(new EmptyBorder(20, 24, 18, 24));

            GraficoJurosPanel grafico = new GraficoJurosPanel(resultados);
            grafico.setBackground(COR_CARD);
            grafico.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA, 1),
                    new EmptyBorder(18, 18, 18, 18)));

            painel.add(grafico, BorderLayout.CENTER);
            return painel;
        }

        private JPanel criarRodape() {
            JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            painel.setBackground(COR_FUNDO);
            painel.setBorder(new EmptyBorder(0, 24, 20, 24));

            JButton fechar = new JButton("Fechar");
            fechar.setFont(FONTE_BTN);
            fechar.setForeground(Color.WHITE);
            fechar.setBackground(COR_AZUL);
            fechar.setBorder(new EmptyBorder(8, 18, 8, 18));
            fechar.setFocusPainted(false);
            fechar.setBorderPainted(false);
            fechar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            fechar.addActionListener(e -> dispose());

            painel.add(fechar);
            return painel;
        }
    }

    private static class GraficoJurosPanel extends JPanel {

        private final List<ResultadoMes> resultados;

        private GraficoJurosPanel(List<ResultadoMes> resultados) {
            this.resultados = resultados;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();
            int esquerda = 70;
            int direita = 28;
            int topo = 28;
            int base = 62;
            int larguraGrafico = largura - esquerda - direita;
            int alturaGrafico = altura - topo - base;

            BigDecimal maximo = resultados.stream()
                    .map(ResultadoMes::montante)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ONE);
            if (maximo.compareTo(BigDecimal.ZERO) <= 0) {
                maximo = BigDecimal.ONE;
            }

            desenharEixos(g2, esquerda, topo, larguraGrafico, alturaGrafico);
            desenharSerie(g2, esquerda, topo, larguraGrafico, alturaGrafico, maximo,
                    ResultadoMes::totalInvestido, COR_LARANJA);
            desenharSerie(g2, esquerda, topo, larguraGrafico, alturaGrafico, maximo,
                    ResultadoMes::montante, COR_AZUL);
            desenharLegenda(g2, largura - direita - 230, topo);
            desenharLabels(g2, esquerda, topo, larguraGrafico, alturaGrafico, maximo);

            g2.dispose();
        }

        private void desenharEixos(Graphics2D g2, int esquerda, int topo, int larguraGrafico, int alturaGrafico) {
            g2.setColor(new Color(226, 232, 240));
            g2.drawLine(esquerda, topo + alturaGrafico, esquerda + larguraGrafico, topo + alturaGrafico);
            g2.drawLine(esquerda, topo, esquerda, topo + alturaGrafico);
        }

        private void desenharSerie(Graphics2D g2, int esquerda, int topo, int larguraGrafico, int alturaGrafico,
                                   BigDecimal maximo, ValorResultado valorResultado, Color cor) {
            int quantidade = resultados.size();
            int pontoAnteriorX = -1;
            int pontoAnteriorY = -1;
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < quantidade; i++) {
                ResultadoMes resultado = resultados.get(i);
                double proporcaoX = quantidade == 1 ? 1.0 : (double) i / (quantidade - 1);
                double proporcaoY = valorResultado.obter(resultado).doubleValue() / maximo.doubleValue();
                int x = esquerda + (int) Math.round(proporcaoX * larguraGrafico);
                int y = topo + alturaGrafico - (int) Math.round(proporcaoY * (alturaGrafico - 12));

                if (pontoAnteriorX >= 0) {
                    g2.drawLine(pontoAnteriorX, pontoAnteriorY, x, y);
                }
                g2.fillOval(x - 4, y - 4, 8, 8);
                pontoAnteriorX = x;
                pontoAnteriorY = y;
            }
        }

        private void desenharLabels(Graphics2D g2, int esquerda, int topo, int larguraGrafico,
                                    int alturaGrafico, BigDecimal maximo) {
            g2.setColor(COR_MUTED);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString(FMT_MOEDA.format(BigDecimal.ZERO), 8, topo + alturaGrafico + 4);
            g2.drawString(FMT_MOEDA.format(maximo.setScale(2, RoundingMode.HALF_UP)), 8, topo + 4);

            int primeiroMes = resultados.get(0).mes();
            int ultimoMes = resultados.get(resultados.size() - 1).mes();
            g2.drawString("Mes " + primeiroMes, esquerda - 6, topo + alturaGrafico + 24);

            String ultimo = "Mes " + ultimoMes;
            int larguraUltimo = g2.getFontMetrics().stringWidth(ultimo);
            g2.drawString(ultimo, esquerda + larguraGrafico - larguraUltimo, topo + alturaGrafico + 24);
        }

        private void desenharLegenda(Graphics2D g2, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            desenharItemLegenda(g2, x, y, COR_AZUL, "Montante");
            desenharItemLegenda(g2, x + 116, y, COR_LARANJA, "Total investido");
        }

        private void desenharItemLegenda(Graphics2D g2, int x, int y, Color cor, String texto) {
            g2.setColor(cor);
            g2.fillRoundRect(x, y - 9, 18, 8, 6, 6);
            g2.setColor(COR_TEXTO);
            g2.drawString(texto, x + 24, y);
        }
    }

    @FunctionalInterface
    private interface ValorResultado {
        BigDecimal obter(ResultadoMes resultado);
    }
}
