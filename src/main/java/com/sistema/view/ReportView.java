package com.sistema.view;

import com.sistema.controller.TransacaoController;
import com.sistema.controller.TransacaoController.ResumoFinanceiro;
import com.sistema.model.Transacao;
import com.sistema.model.TipoTransacao;
import com.sistema.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Diálogo de relatório financeiro por período.
 * Permite filtrar transações por data e visualizar resumo de receitas,
 * despesas e saldo do período selecionado.
 */
public class ReportView extends JDialog {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color COR_FUNDO    = new Color(15, 23, 42);
    private static final Color COR_CARD     = new Color(30, 41, 59);
    private static final Color COR_BORDA    = new Color(51, 65, 85);
    private static final Color COR_VERDE    = new Color(34, 197, 94);
    private static final Color COR_VERMELHO = new Color(239, 68, 68);
    private static final Color COR_AZUL     = new Color(59, 130, 246);
    private static final Color COR_AMARELO  = new Color(234, 179, 8);
    private static final Color COR_TEXTO    = new Color(241, 245, 249);
    private static final Color COR_MUTED    = new Color(148, 163, 184);
    private static final Color COR_INPUT    = new Color(15, 23, 42);

    private static final Font FONTE_TITULO   = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_LABEL    = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_INPUT    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_TABELA   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONTE_RESUMO   = new Font("Segoe UI", Font.BOLD, 15);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Componentes ───────────────────────────────────────────────────────────
    private JTextField       txtDataInicio;
    private JTextField       txtDataFim;
    private JLabel           lblReceitas;
    private JLabel           lblDespesas;
    private JLabel           lblSaldo;
    private DefaultTableModel tableModel;

    private final TransacaoController transacaoController;

    // ── Construtor ────────────────────────────────────────────────────────────

    public ReportView(Frame owner) {
        super(owner, "Relatório Financeiro", true);
        this.transacaoController = new TransacaoController();
        construirInterface();
        // Carrega mês atual como padrão
        LocalDate hoje   = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        txtDataInicio.setText(inicio.format(FMT));
        txtDataFim.setText(hoje.format(FMT));
        gerarRelatorio();
    }

    // ── Construção ────────────────────────────────────────────────────────────

    private void construirInterface() {
        setSize(800, 680);
        setMinimumSize(new Dimension(700, 560));
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(),  BorderLayout.NORTH);
        add(criarCorpo(),      BorderLayout.CENTER);
        add(criarBotaoFechar(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COR_CARD);
        p.setBorder(new EmptyBorder(20, 24, 16, 24));
        JLabel lbl = new JLabel("📊  Relatório Financeiro");
        lbl.setFont(FONTE_TITULO);
        lbl.setForeground(COR_TEXTO);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JPanel criarCorpo() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(20, 24, 8, 24));

        p.add(criarPainelFiltro(),  BorderLayout.NORTH);
        p.add(criarPainelResultado(), BorderLayout.CENTER);
        return p;
    }

    /** Painel de filtros de data e botão gerar */
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
        linha.add(txtDataInicio);

        linha.add(criarLabelInline("Até:"));
        txtDataFim = criarCampoData();
        linha.add(txtDataFim);

        JButton btnGerar = new JButton("🔍 Gerar Relatório");
        btnGerar.setFont(FONTE_LABEL);
        btnGerar.setForeground(COR_TEXTO);
        btnGerar.setBackground(COR_AZUL);
        btnGerar.setBorder(new EmptyBorder(8, 18, 8, 18));
        btnGerar.setFocusPainted(false);
        btnGerar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGerar.setOpaque(true);
        btnGerar.addActionListener(e -> gerarRelatorio());
        linha.add(btnGerar);

        painel.add(lbl,  BorderLayout.NORTH);
        painel.add(linha, BorderLayout.CENTER);
        return painel;
    }

    /** Painel com cards de resumo + tabela detalhada */
    private JPanel criarPainelResultado() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(COR_FUNDO);

        // Cards de resumo
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBackground(COR_FUNDO);

        lblReceitas = new JLabel("R$ 0,00");
        cards.add(criarCardResumo("📈 Receitas",  lblReceitas, COR_VERDE));

        lblDespesas = new JLabel("R$ 0,00");
        cards.add(criarCardResumo("📉 Despesas",  lblDespesas, COR_VERMELHO));

        lblSaldo    = new JLabel("R$ 0,00");
        cards.add(criarCardResumo("💳 Saldo",     lblSaldo,    COR_AZUL));

        // Tabela detalhada
        String[] colunas = {"Data", "Descrição", "Categoria", "Tipo", "Valor"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(tableModel);
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(32);
        tabela.setBackground(COR_CARD);
        tabela.setForeground(COR_TEXTO);
        tabela.setSelectionBackground(COR_BORDA);
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setGridColor(COR_BORDA);
        tabela.setShowGrid(true);
        tabela.setFocusable(false);
        tabela.getTableHeader().setFont(FONTE_LABEL);
        tabela.getTableHeader().setBackground(COR_BORDA);
        tabela.getTableHeader().setForeground(COR_MUTED);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabela.getTableHeader().setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(110);

        // Renderer da coluna Tipo
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (val instanceof TipoTransacao tipo) {
                    setForeground(tipo == TipoTransacao.RECEITA ? COR_VERDE : COR_VERMELHO);
                    setText(tipo == TipoTransacao.RECEITA ? "▲ Receita" : "▼ Despesa");
                }
                setBackground(sel ? COR_BORDA : COR_CARD);
                return this;
            }
        });

        // Renderer valor
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(RIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(COR_TEXTO);
                setBackground(sel ? COR_BORDA : COR_CARD);
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

    private JPanel criarBotaoFechar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(0, 24, 16, 24));

        JButton btn = new JButton("Fechar");
        btn.setFont(FONTE_LABEL);
        btn.setForeground(COR_TEXTO);
        btn.setBackground(new Color(71, 85, 105));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addActionListener(e -> dispose());
        p.add(btn);
        return p;
    }

    // ── Lógica ───────────────────────────────────────────────────────────────

    private void gerarRelatorio() {
        try {
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText().trim(), FMT);
            LocalDate fim    = LocalDate.parse(txtDataFim.getText().trim(), FMT);

            ResumoFinanceiro resumo = transacaoController.gerarResumo(inicio, fim);

            // Atualiza cards
            lblReceitas.setText(CurrencyUtil.formatar(resumo.totalReceitas()));
            lblDespesas.setText(CurrencyUtil.formatar(resumo.totalDespesas()));
            lblSaldo.setText(CurrencyUtil.formatar(resumo.saldo()));
            lblSaldo.setForeground(resumo.saldo().signum() >= 0 ? COR_VERDE : COR_VERMELHO);

            // Atualiza tabela
            tableModel.setRowCount(0);
            for (Transacao t : resumo.transacoes()) {
                tableModel.addRow(new Object[]{
                        t.getData().format(FMT),
                        t.getDescricao(),
                        t.getCategoria() != null ? t.getCategoria().getNome() : "—",
                        t.getTipo(),
                        CurrencyUtil.formatar(t.getValor())
                });
            }

            if (resumo.transacoes().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma transação encontrada no período selecionado.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }

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
}