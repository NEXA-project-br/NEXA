package com.sistema.view;

import com.sistema.controller.TransacaoController;
import com.sistema.controller.TransacaoController.ResumoFinanceiro;
import com.sistema.model.Transacao;
import com.sistema.model.TipoTransacao;
import com.sistema.util.CurrencyUtil;
import com.sistema.util.PdfReportExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutionException;

/**
 * Janela responsavel por gerar e exibir relatorios financeiros.
 */
public class ReportView extends JDialog {

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_FUNDO    = new Color(248, 250, 252);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_CARD     = new Color(255, 255, 255);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_BORDA    = new Color(203, 213, 225);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_VERDE    = new Color(34, 197, 94);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_VERMELHO = new Color(220, 60, 60);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_AZUL_ESCURO = new Color(29, 78, 150);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_AZUL_CLARO  = new Color(59, 130, 246);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_GRAFITE  = new Color(100, 116, 139);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_TEXTO    = new Color(15, 23, 42);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_MUTED    = new Color(71, 85, 105);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_INPUT    = new Color(255, 255, 255);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_TITULO  = new Font("Segoe UI", Font.BOLD, 18);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_LABEL   = new Font("Segoe UI", Font.BOLD, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_TABELA  = new Font("Segoe UI", Font.PLAIN, 12);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_RESUMO  = new Font("Segoe UI", Font.BOLD, 15);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_BTN     = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Formato usado para compor o nome dos arquivos de backup.
     */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField        txtDataInicio;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField        txtDataFim;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel            lblReceitas;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel            lblDespesas;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel            lblSaldo;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private DefaultTableModel tableModel;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private ResumoFinanceiro  resumoAtual;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JButton           btnExportar;

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final TransacaoController transacaoController;

    /**
     * Cria uma nova instancia de ReportView.
     *
     * @param owner janela proprietaria do dialogo
     */
    public ReportView(Frame owner) {
        super(owner, "Relatório Financeiro", true);
        AppIconUtil.aplicar(this);
        this.transacaoController = new TransacaoController();
        construirInterface();
        LocalDate hoje = LocalDate.now();
        txtDataInicio.setText(hoje.withDayOfMonth(1).format(FMT));
        txtDataFim.setText(hoje.format(FMT));
        gerarRelatorio();
    }

    /**
     * Monta os componentes visuais da tela.
     */
    private void construirInterface() {
        setSize(820, 680);
        setMinimumSize(new Dimension(700, 560));
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(),    BorderLayout.NORTH);
        add(criarCorpo(),        BorderLayout.CENTER);
        add(criarPainelAcoes(),  BorderLayout.SOUTH);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COR_CARD);
        p.setBorder(new EmptyBorder(20, 24, 16, 24));
        JLabel lbl = new JLabel("Relatório Financeiro por Período");
        lbl.setFont(FONTE_TITULO);
        lbl.setForeground(COR_TEXTO);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarCorpo() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(20, 24, 8, 24));
        p.add(criarPainelFiltro(),    BorderLayout.NORTH);
        p.add(criarPainelResultado(), BorderLayout.CENTER);
        return p;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarPainelFiltro() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel lbl = new JLabel("Período");
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        linha.setBackground(COR_CARD);

        linha.add(criarLabelInline("De:"));
        txtDataInicio = criarCampoData();
        aplicarMascaraData(txtDataInicio);
        linha.add(DatePickerUtil.criarCampoComCalendario(txtDataInicio));

        linha.add(criarLabelInline("Até:"));
        txtDataFim = criarCampoData();
        aplicarMascaraData(txtDataFim);
        linha.add(DatePickerUtil.criarCampoComCalendario(txtDataFim));

        JButton btnGerar = criarBotao("Filtrar", COR_AZUL_CLARO, UiIcons.report(Color.WHITE));
        btnGerar.addActionListener(e -> gerarRelatorio());
        linha.add(btnGerar);

        painel.add(lbl,  BorderLayout.NORTH);
        painel.add(linha, BorderLayout.CENTER);
        return painel;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarPainelResultado() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(COR_FUNDO);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBackground(COR_FUNDO);

        lblReceitas = new JLabel("R$ 0,00");
        cards.add(criarCardResumo("Receitas", lblReceitas, COR_VERDE));

        lblDespesas = new JLabel("R$ 0,00");
        cards.add(criarCardResumo("Despesas", lblDespesas, COR_VERMELHO));

        lblSaldo = new JLabel("R$ 0,00");
        cards.add(criarCardResumo("Saldo do Período", lblSaldo, COR_AZUL_CLARO));

        // Tabela
        String[] colunas = {"Data", "Descrição", "Categoria", "Tipo", "Valor"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(tableModel);
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(32);
        tabela.setBackground(COR_CARD);
        tabela.setForeground(COR_TEXTO);
        tabela.setSelectionBackground(new Color(219, 234, 254));
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setGridColor(COR_BORDA);
        tabela.setShowGrid(true);
        tabela.setFocusable(false);
        tabela.getTableHeader().setFont(FONTE_LABEL);
        tabela.getTableHeader().setBackground(new Color(241, 245, 249));
        tabela.getTableHeader().setForeground(COR_TEXTO);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabela.getTableHeader().setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(110);

        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            /**
             * Prepara o componente usado para renderizar uma celula da tabela.
             *
             * @param t tabela ou lista relacionada a renderizacao
             * @param val valor exibido na celula
             * @param sel parametro sel
             * @param foc parametro foc
             * @param row linha da celula
             * @param col coluna da celula
             * @return componente preparado para renderizacao
             */
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (val instanceof TipoTransacao tipo) {
                    if (tipo == TipoTransacao.RECEITA) {
                        setForeground(COR_VERDE);
                        setText("Receita");
                    } else {
                        setForeground(COR_VERMELHO);
                        setText("Despesa");
                    }
                }
                setBackground(sel ? new Color(219, 234, 254) : Color.WHITE);
                return this;
            }
        });

        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            /**
             * Prepara o componente usado para renderizar uma celula da tabela.
             *
             * @param t tabela ou lista relacionada a renderizacao
             * @param val valor exibido na celula
             * @param sel parametro sel
             * @param foc parametro foc
             * @param row linha da celula
             * @param col coluna da celula
             * @return componente preparado para renderizacao
             */
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(RIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(COR_TEXTO);
                setBackground(sel ? new Color(219, 234, 254) : Color.WHITE);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(COR_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));

        p.add(cards,  BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param titulo parametro titulo
     * @param valor parametro valor
     * @param cor parametro cor
     * @return painel configurado
     */
    private JPanel criarCardResumo(String titulo, JLabel valor, Color cor) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(COR_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(16, 20, 16, 20)));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(COR_MUTED);
        valor.setFont(FONTE_RESUMO);
        valor.setForeground(cor);
        valor.setBorder(new EmptyBorder(6, 0, 0, 0));
        c.add(lbl,   BorderLayout.NORTH);
        c.add(valor, BorderLayout.CENTER);
        return c;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarPainelAcoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(4, 24, 16, 24));

        btnExportar = criarBotao("Exportar para PDF", COR_AZUL_ESCURO, UiIcons.pdf(Color.WHITE));
        btnExportar.setEnabled(false);
        btnExportar.addActionListener(e -> exportarPdf());

        JButton btnFechar = criarBotao("Fechar", COR_GRAFITE, UiIcons.close(Color.WHITE));
        btnFechar.addActionListener(e -> dispose());

        p.add(btnExportar);
        p.add(btnFechar);
        return p;
    }

    // ── Logica ────────────────────────────────────────────────────────────────

    /**
     * Executa a rotina gerarRelatorio.
     */
    private void gerarRelatorio() {
        try {
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText().trim(), FMT);
            LocalDate fim    = LocalDate.parse(txtDataFim.getText().trim(), FMT);
            btnExportar.setEnabled(false);
            resumoAtual = null;

            new SwingWorker<ResumoFinanceiro, Void>() {
                /**
                 * Executa a rotina doInBackground.
                 *
                 * @return resultado da operacao
                 */
                @Override
                protected ResumoFinanceiro doInBackground() {
                    return transacaoController.gerarResumo(inicio, fim);
                }

                /**
                 * Executa a rotina done.
                 */
                @Override
                protected void done() {
                    try {
                        ResumoFinanceiro resumo = get();
                        resumoAtual = resumo;

                        lblReceitas.setText(CurrencyUtil.formatar(resumo.totalReceitas()));
                        lblDespesas.setText(CurrencyUtil.formatar(resumo.totalDespesas()));
                        lblSaldo.setText(CurrencyUtil.formatar(resumo.saldo()));
                        lblSaldo.setForeground(resumo.saldo().signum() >= 0 ? COR_VERDE : COR_VERMELHO);

                        tableModel.setRowCount(0);
                        for (Transacao t : resumo.transacoes()) {
                            tableModel.addRow(new Object[]{
                                    t.getData().format(FMT),
                                    t.getDescricao(),
                                    t.getCategoria() != null ? t.getCategoria().getNome() : "-",
                                    t.getTipo(),
                                    CurrencyUtil.formatar(t.getValor())
                            });
                        }
                        btnExportar.setEnabled(true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReportView.this,
                                "Erro ao gerar relatório: " + mensagemErroWorker(ex),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data inválida. Use o formato dd/MM/yyyy.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Executa a rotina exportarPdf.
     */
    private void exportarPdf() {
        if (resumoAtual == null) {
            JOptionPane.showMessageDialog(this,
                    "Aguarde o relatório terminar de carregar antes de exportar.",
                    "Relatório ainda carregando", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar relatório em PDF");
        chooser.setSelectedFile(new java.io.File(nomeArquivoPadrao()));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destino = garantirExtensaoPdf(chooser.getSelectedFile().toPath());
        if (destino.toFile().exists()) {
            int sobrescrever = JOptionPane.showConfirmDialog(this,
                    "O arquivo ja existe. Deseja substituir?",
                    "Confirmar exportação", JOptionPane.YES_NO_OPTION);
            if (sobrescrever != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            PdfReportExporter.exportar(resumoAtual, destino);
            JOptionPane.showMessageDialog(this,
                    "Relatório exportado com sucesso:\n" + destino,
                    "PDF gerado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar PDF:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @return rotulo configurado
     */
    private JLabel criarLabelInline(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        return lbl;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return campo de texto configurado
     */
    private JTextField criarCampoData() {
        JTextField tf = new JTextField(10);
        tf.setFont(FONTE_INPUT);
        tf.setForeground(COR_TEXTO);
        tf.setBackground(COR_INPUT);
        tf.setCaretColor(COR_TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param cor parametro cor
     * @return botao configurado
     */
    private JButton criarBotao(String texto, Color cor) {
        return criarBotao(texto, cor, null);
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
        btn.setPreferredSize(new Dimension(Math.max(120, btn.getPreferredSize().width), 38));
        return btn;
    }

    /**
     * Executa a rotina aplicarMascaraData.
     *
     * @param campo parametro campo
     */
    private void aplicarMascaraData(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DateDocumentFilter());
    }

    /**
     * Executa a rotina nomeArquivoPadrao.
     *
     * @return texto formatado
     */
    private String nomeArquivoPadrao() {
        String inicio = resumoAtual.inicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fim = resumoAtual.fim().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "relatorio-financeiro-" + inicio + "-a-" + fim + ".pdf";
    }

    /**
     * Garante que o arquivo escolhido termine com a extensao .pdf.
     *
     * @param caminho arquivo escolhido pelo usuario
     * @return caminho com extensao PDF
     */
    private Path garantirExtensaoPdf(Path caminho) {
        String texto = caminho.toString();
        return texto.toLowerCase().endsWith(".pdf") ? caminho : Path.of(texto + ".pdf");
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
}
