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
import java.util.concurrent.ExecutionException;

/**
 * Janela responsavel por exibir graficos mensais de transacoes.
 */
public class MonthlyChartView extends JDialog {

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
    private static final Color COR_TEXTO = new Color(15, 23, 42);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_MUTED = new Color(71, 85, 105);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_VERDE = new Color(34, 197, 94);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_VERMELHO = new Color(220, 60, 60);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_AZUL = new Color(59, 130, 246);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_RESUMO = new Font("Segoe UI", Font.BOLD, 14);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_BTN = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final TransacaoController transacaoController;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final TipoTransacao tipo;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel lblResumo;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JPanel corpoWrapper;

    /**
     * Cria uma nova instancia de MonthlyChartView.
     *
     * @param owner janela proprietaria do dialogo
     * @param tipo parametro tipo
     */
    public MonthlyChartView(Frame owner, TipoTransacao tipo) {
        super(owner, tipo == TipoTransacao.RECEITA ? "Gráfico de Receita Mensal" : "Gráfico de Despesa Mensal", true);
        AppIconUtil.aplicar(this);
        this.transacaoController = new TransacaoController();
        this.tipo = tipo;
        construirInterface();
    }

    /**
     * Monta os componentes visuais da tela.
     */
    private void construirInterface() {
        setSize(860, 540);
        setMinimumSize(new Dimension(720, 480));
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        corpoWrapper = new JPanel(new BorderLayout());
        corpoWrapper.setBackground(COR_FUNDO);
        add(corpoWrapper, BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
        carregarDadosAsync();
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

        JLabel titulo = new JLabel(tipo == TipoTransacao.RECEITA ? "Receitas por Mês" : "Despesas por Mês");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO);

        lblResumo = new JLabel();
        lblResumo.setFont(FONTE_RESUMO);
        lblResumo.setForeground(COR_MUTED);

        painel.add(titulo, BorderLayout.WEST);
        painel.add(lblResumo, BorderLayout.EAST);
        return painel;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param dados parametro dados
     * @return painel configurado
     */
    private JPanel criarCorpo(Map<YearMonth, BigDecimal> dados) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 24, 20, 24));

        if (dados.isEmpty()) {
            JLabel vazio = new JLabel("Não há dados para exibir neste gráfico.", SwingConstants.CENTER);
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
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

    /**
     * Executa a rotina carregarDados.
     *
     * @return resultado da operacao
     */
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

    /**
     * Executa a rotina carregarDadosAsync.
     */
    private void carregarDadosAsync() {
        new SwingWorker<Map<YearMonth, BigDecimal>, Void>() {
            /**
             * Executa a rotina doInBackground.
             *
             * @return resultado da operacao
             */
            @Override
            protected Map<YearMonth, BigDecimal> doInBackground() {
                return carregarDados();
            }

            /**
             * Executa a rotina done.
             */
            @Override
            protected void done() {
                try {
                    corpoWrapper.removeAll();
                    corpoWrapper.add(criarCorpo(get()), BorderLayout.CENTER);
                    corpoWrapper.revalidate();
                    corpoWrapper.repaint();
                } catch (Exception e) {
                    lblResumo.setText("Erro ao carregar dados");
                    JOptionPane.showMessageDialog(MonthlyChartView.this,
                            "Erro ao carregar grafico: " + mensagemErroWorker(e),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Executa a rotina mensagemErroWorker.
     *
     * @param ex parametro ex
     * @return texto formatado
     */
    private String mensagemErroWorker(Exception ex) {
        Throwable causa = ex instanceof ExecutionException ? ex.getCause() : ex;
        if (causa instanceof IllegalArgumentException) {
            return causa.getMessage();
        }
        return "Não foi possível concluir a operação.";
    }

    /**
     * Tipo responsavel por funcionalidades de ChartPanel.
     */
    private static class ChartPanel extends JPanel {

        /**
         * Formato usado para compor o nome dos arquivos de backup.
         */
        private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/yyyy");

        /**
         * Atributo usado pelo funcionamento desta classe.
         */
        private final Map<YearMonth, BigDecimal> dados;
        /**
         * Atributo usado pelo funcionamento desta classe.
         */
        private final TipoTransacao tipo;

        /**
         * Cria o painel de grafico mensal.
         *
         * @param dados dados agrupados por mes
         * @param tipo tipo de transacao exibido
         */
        private ChartPanel(Map<YearMonth, BigDecimal> dados, TipoTransacao tipo) {
            this.dados = dados;
            this.tipo = tipo;
        }

        /**
         * Executa a rotina paintComponent.
         *
         * @param g contexto grafico usado no desenho
         */
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
