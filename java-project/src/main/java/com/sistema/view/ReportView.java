package com.sistema.view;

import com.sistema.controller.TransacaoController;
import com.sistema.controller.TransacaoController.ResumoFinanceiro;
import com.sistema.model.Transacao;
import com.sistema.model.TipoTransacao;
import com.sistema.util.CurrencyUtil;
import com.sistema.util.PdfReportExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReportView extends JDialog {

    private static final Color COR_FUNDO    = new Color(248, 250, 252);
    private static final Color COR_CARD     = new Color(255, 255, 255);
    private static final Color COR_BORDA    = new Color(203, 213, 225);
    private static final Color COR_VERDE    = new Color(34, 197, 94);
    private static final Color COR_VERMELHO = new Color(220, 60, 60);
    private static final Color COR_AZUL_ESCURO = new Color(29, 78, 150);
    private static final Color COR_AZUL_CLARO  = new Color(59, 130, 246);
    private static final Color COR_GRAFITE  = new Color(100, 116, 139);
    private static final Color COR_TEXTO    = new Color(15, 23, 42);
    private static final Color COR_MUTED    = new Color(71, 85, 105);
    private static final Color COR_INPUT    = new Color(255, 255, 255);

    private static final Font FONTE_TITULO  = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_LABEL   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_TABELA  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONTE_RESUMO  = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONTE_BTN     = new Font("Segoe UI", Font.BOLD, 13);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField        txtDataInicio;
    private JTextField        txtDataFim;
    private JLabel            lblReceitas;
    private JLabel            lblDespesas;
    private JLabel            lblSaldo;
    private DefaultTableModel tableModel;
    private ResumoFinanceiro  resumoAtual;

    private final TransacaoController transacaoController;

    public ReportView(Frame owner) {
        super(owner, "Relatorio Financeiro", true);
        this.transacaoController = new TransacaoController();
        construirInterface();
        LocalDate hoje = LocalDate.now();
        txtDataInicio.setText(hoje.withDayOfMonth(1).format(FMT));
        txtDataFim.setText(hoje.format(FMT));
        gerarRelatorio();
    }

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

    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COR_CARD);
        p.setBorder(new EmptyBorder(20, 24, 16, 24));
        JLabel lbl = new JLabel("Relatorio Financeiro por Periodo");
        lbl.setFont(FONTE_TITULO);
        lbl.setForeground(COR_TEXTO);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JPanel criarCorpo() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(20, 24, 8, 24));
        p.add(criarPainelFiltro(),    BorderLayout.NORTH);
        p.add(criarPainelResultado(), BorderLayout.CENTER);
        return p;
    }

    private JPanel criarPainelFiltro() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel lbl = new JLabel("Periodo");
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        linha.setBackground(COR_CARD);

        linha.add(criarLabelInline("De:"));
        txtDataInicio = criarCampoData();
        aplicarMascaraData(txtDataInicio);
        linha.add(txtDataInicio);

        linha.add(criarLabelInline("Ate:"));
        txtDataFim = criarCampoData();
        aplicarMascaraData(txtDataFim);
        linha.add(txtDataFim);

        JButton btnGerar = criarBotao("Filtrar", COR_AZUL_CLARO, UiIcons.report(Color.WHITE));
        btnGerar.addActionListener(e -> gerarRelatorio());
        linha.add(btnGerar);

        painel.add(lbl,  BorderLayout.NORTH);
        painel.add(linha, BorderLayout.CENTER);
        return painel;
    }

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
        cards.add(criarCardResumo("Saldo do Periodo", lblSaldo, COR_AZUL_CLARO));

        // Tabela
        String[] colunas = {"Data", "Descricao", "Categoria", "Tipo", "Valor"};
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

    private JPanel criarPainelAcoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(4, 24, 16, 24));

        JButton btnExportar = criarBotao("Exportar para PDF", COR_AZUL_ESCURO, UiIcons.pdf(Color.WHITE));
        btnExportar.addActionListener(e -> exportarPdf());

        JButton btnFechar = criarBotao("Fechar", COR_GRAFITE, UiIcons.close(Color.WHITE));
        btnFechar.addActionListener(e -> dispose());

        p.add(btnExportar);
        p.add(btnFechar);
        return p;
    }

    // ── Logica ────────────────────────────────────────────────────────────────

    private void gerarRelatorio() {
        try {
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText().trim(), FMT);
            LocalDate fim    = LocalDate.parse(txtDataFim.getText().trim(), FMT);

            ResumoFinanceiro resumo = transacaoController.gerarResumo(inicio, fim);
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

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data invalida. Use o formato dd/MM/yyyy.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void exportarPdf() {
        if (resumoAtual == null) {
            gerarRelatorio();
        }
        if (resumoAtual == null) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar relatorio para PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File(nomeArquivoPadrao()));

        int escolha = chooser.showSaveDialog(this);
        if (escolha != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destino = garantirExtensaoPdf(chooser.getSelectedFile()).toPath();
        if (destino.toFile().exists()) {
            int sobrescrever = JOptionPane.showConfirmDialog(this,
                    "O arquivo ja existe. Deseja substituir?",
                    "Confirmar exportacao", JOptionPane.YES_NO_OPTION);
            if (sobrescrever != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            PdfReportExporter.exportar(resumoAtual, destino);
            JOptionPane.showMessageDialog(this,
                    "Relatorio exportado com sucesso:\n" + destino,
                    "PDF gerado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar PDF:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel criarLabelInline(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        return lbl;
    }

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

    private JButton criarBotao(String texto, Color cor) {
        return criarBotao(texto, cor, null);
    }

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

    private void aplicarMascaraData(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DateDocumentFilter());
    }

    private String nomeArquivoPadrao() {
        String inicio = resumoAtual.inicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fim = resumoAtual.fim().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "relatorio-financeiro-" + inicio + "-a-" + fim + ".pdf";
    }

    private File garantirExtensaoPdf(File arquivo) {
        String nome = arquivo.getName().toLowerCase();
        if (nome.endsWith(".pdf")) {
            return arquivo;
        }
        return new File(arquivo.getParentFile(), arquivo.getName() + ".pdf");
    }
}
