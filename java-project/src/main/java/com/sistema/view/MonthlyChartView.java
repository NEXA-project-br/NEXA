package com.sistema.view;

import com.sistema.controller.TransacaoController;
import com.sistema.model.TipoTransacao;
import com.sistema.model.Transacao;
import com.sistema.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MonthlyChartView extends JDialog {

    private static final Color COR_FUNDO = new Color(248, 250, 252);
    private static final Color COR_CARD = new Color(255, 255, 255);
    private static final Color COR_BORDA = new Color(203, 213, 225);
    private static final Color COR_TEXTO = new Color(15, 23, 42);
    private static final Color COR_MUTED = new Color(71, 85, 105);
    private static final Color COR_VERDE = new Color(34, 197, 94);
    private static final Color COR_VERMELHO = new Color(220, 60, 60);
    private static final Color COR_AZUL = new Color(59, 130, 246);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_RESUMO = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_BTN = new Font("Segoe UI", Font.BOLD, 13);

    private final TransacaoController transacaoController;
    private final TipoTransacao tipo;
    private JLabel lblResumo;

    public MonthlyChartView(Frame owner, TipoTransacao tipo) {
        super(owner, tipo == TipoTransacao.RECEITA ? "Grafico de Receita Mensal" : "Grafico de Despesa Mensal", true);
        this.transacaoController = new TransacaoController();
        this.tipo = tipo;
        construirInterface();
    }

    private void construirInterface() {
        setSize(860, 540);
        setMinimumSize(new Dimension(720, 480));
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        Map<YearMonth, BigDecimal> dados = carregarDados();

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpo(dados), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel titulo = new JLabel(tipo == TipoTransacao.RECEITA ? "Receitas por Mes" : "Despesas por Mes");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO);

        lblResumo = new JLabel();
        lblResumo.setFont(FONTE_RESUMO);
        lblResumo.setForeground(COR_MUTED);

        painel.add(titulo, BorderLayout.WEST);
        painel.add(lblResumo, BorderLayout.EAST);
        return painel;
    }

    private JPanel criarCorpo(Map<YearMonth, BigDecimal> dados) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 24, 20, 24));

        if (dados.isEmpty()) {
            JLabel vazio = new JLabel("Nao ha dados para exibir neste grafico.", SwingConstants.CENTER);
            vazio.setFont(new Font("Segoe UI", Font.BOLD, 16));
            vazio.setForeground(COR_MUTED);
            vazio.setOpaque(true);
            vazio.setBackground(COR_CARD);
            vazio.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA, 1),
                    new EmptyBorder(24, 24, 24, 24)));
            lblResumo.setText("Total: R$ 0,00");
            painel.add(vazio, BorderLayout.CENTER);
            return painel;
        }

        BigDecimal total = dados.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        lblResumo.setText("Total: " + CurrencyUtil.formatar(total));

        ChartPanel chartPanel = new ChartPanel(dados, tipo);
        chartPanel.setBackground(COR_CARD);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(20, 20, 20, 20)));

        painel.add(chartPanel, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarRodape() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 24, 20, 24));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setFont(FONTE_BTN);
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setBackground(COR_AZUL);
        btnFechar.setBorder(new EmptyBorder(8, 18, 8, 18));
        btnFechar.setFocusPainted(false);
        btnFechar.setBorderPainted(false);
        btnFechar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFechar.addActionListener(e -> dispose());

        painel.add(btnFechar);
        return painel;
    }

    private Map<YearMonth, BigDecimal> carregarDados() {
        List<Transacao> transacoes = transacaoController.listarTodos();
        Map<YearMonth, BigDecimal> agrupado = new LinkedHashMap<>();

        for (Transacao transacao : transacoes) {
            if (transacao.getTipo() != tipo) {
                continue;
            }
            YearMonth mes = YearMonth.from(transacao.getData());
            agrupado.merge(mes, transacao.getValor(), BigDecimal::add);
        }

        return agrupado.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll);
    }

    private static class ChartPanel extends JPanel {

        private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/yyyy");

        private final Map<YearMonth, BigDecimal> dados;
        private final TipoTransacao tipo;

        private ChartPanel(Map<YearMonth, BigDecimal> dados, TipoTransacao tipo) {
            this.dados = dados;
            this.tipo = tipo;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();
            int margemEsquerda = 60;
            int margemDireita = 20;
            int margemTopo = 20;
            int margemBase = 60;

            int larguraGrafico = largura - margemEsquerda - margemDireita;
            int alturaGrafico = altura - margemTopo - margemBase;

            BigDecimal maximo = dados.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
            if (maximo.compareTo(BigDecimal.ZERO) == 0) {
                maximo = BigDecimal.ONE;
            }

            g2.setColor(new Color(226, 232, 240));
            g2.drawLine(margemEsquerda, margemTopo + alturaGrafico, largura - margemDireita, margemTopo + alturaGrafico);
            g2.drawLine(margemEsquerda, margemTopo, margemEsquerda, margemTopo + alturaGrafico);

            int quantidade = dados.size();
            int espaco = Math.max(16, larguraGrafico / Math.max(quantidade, 1));
            int barraLargura = Math.max(22, Math.min(48, espaco - 12));
            int i = 0;

            for (Map.Entry<YearMonth, BigDecimal> entry : dados.entrySet()) {
                double proporcao = entry.getValue().doubleValue() / maximo.doubleValue();
                int barraAltura = (int) Math.round(proporcao * (alturaGrafico - 20));
                int x = margemEsquerda + i * espaco + Math.max(0, (espaco - barraLargura) / 2);
                int y = margemTopo + alturaGrafico - barraAltura;

                g2.setColor(tipo == TipoTransacao.RECEITA ? COR_VERDE : COR_VERMELHO);
                g2.fillRoundRect(x, y, barraLargura, barraAltura, 8, 8);

                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                String mes = entry.getKey().format(FMT);
                int labelWidth = g2.getFontMetrics().stringWidth(mes);
                g2.drawString(mes, x + (barraLargura - labelWidth) / 2, margemTopo + alturaGrafico + 18);

                String valor = "R$ " + entry.getValue().setScale(0, RoundingMode.HALF_UP).toPlainString();
                int valorWidth = g2.getFontMetrics().stringWidth(valor);
                g2.drawString(valor, x + (barraLargura - valorWidth) / 2, Math.max(14, y - 6));
                i++;
            }

            g2.dispose();
        }
    }
}
