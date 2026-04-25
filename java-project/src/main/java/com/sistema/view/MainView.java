package com.sistema.view;

import com.sistema.controller.TransacaoController;
import com.sistema.model.Transacao;
import com.sistema.model.TipoTransacao;
import com.sistema.util.CurrencyUtil;
import com.sistema.util.HibernateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainView extends JFrame {

    private static final Color COR_FUNDO       = new Color(248, 250, 252);
    private static final Color COR_CARD        = new Color(255, 255, 255);
    private static final Color COR_BORDA       = new Color(203, 213, 225);
    private static final Color COR_VERDE       = new Color(34, 197, 94);
    private static final Color COR_VERMELHO    = new Color(220, 60, 60);
    private static final Color COR_AZUL_ESCURO = new Color(29, 78, 150);
    private static final Color COR_AZUL_CLARO  = new Color(59, 130, 246);
    private static final Color COR_GRAFITE     = new Color(100, 116, 139);
    private static final Color COR_TEXTO       = new Color(15, 23, 42);
    private static final Color COR_MUTED       = new Color(71, 85, 105);

    private static final Font FONTE_TITULO   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONTE_CARD_VAL = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONTE_CARD_LBL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_TABELA   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_HEADER   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_BTN      = new Font("Segoe UI", Font.BOLD, 13);

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblSaldo;
    private JLabel lblTotalReceitas;
    private JLabel lblTotalDespesas;
    private DefaultTableModel tableModel;
    private JTable tabela;
    private List<Transacao> listaAtual = List.of();

    private final TransacaoController transacaoController;

    public MainView() {
        this.transacaoController = new TransacaoController();
        configurarJanela();
        construirInterface();
        atualizarDashboard();
    }

    private void configurarJanela() {
        setTitle("Sistema Financeiro Pessoal");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                encerrarAplicacao();
            }
        });
    }

    private void construirInterface() {
        setLayout(new BorderLayout());
        add(criarPainelTopo(),    BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
    }

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titulo = new JLabel("Painel Financeiro");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnReceita = criarBotao("Nova Receita", COR_VERDE);
        JButton btnDespesa = criarBotao("Nova Despesa", COR_VERMELHO);
        JButton btnCats    = criarBotao("Categorias",   COR_GRAFITE);
        JButton btnRelat   = criarBotao("Relatorios",   COR_AZUL_ESCURO);
        JButton btnCalc    = criarBotao("Calculadora",  COR_AZUL_CLARO);
        JButton btnGrafRec = criarBotao("Receita Mensal", COR_VERDE);
        JButton btnGrafDes = criarBotao("Despesa Mensal", COR_VERMELHO);

        btnReceita.addActionListener(e -> abrirFormularioNovaTransacao(TipoTransacao.RECEITA));
        btnDespesa.addActionListener(e -> abrirFormularioNovaTransacao(TipoTransacao.DESPESA));
        btnCats.addActionListener(e    -> abrirCategorias());
        btnRelat.addActionListener(e   -> abrirRelatorios());
        btnCalc.addActionListener(e    -> abrirCalculadora());
        btnGrafRec.addActionListener(e -> abrirGraficoMensal(TipoTransacao.RECEITA));
        btnGrafDes.addActionListener(e -> abrirGraficoMensal(TipoTransacao.DESPESA));

        btnPanel.add(btnGrafDes);
        btnPanel.add(btnGrafRec);
        btnPanel.add(btnCalc);
        btnPanel.add(btnRelat);
        btnPanel.add(btnCats);
        btnPanel.add(btnDespesa);
        btnPanel.add(btnReceita);

        painel.add(titulo,   BorderLayout.WEST);
        painel.add(btnPanel, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COR_FUNDO);
        wrapper.add(painel, BorderLayout.CENTER);
        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        wrapper.add(sep, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel criarPainelCentral() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 24, 20, 24));
        painel.add(criarPainelCards(),  BorderLayout.NORTH);
        painel.add(criarPainelTabela(), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelCards() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 16, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 0, 20, 0));

        lblSaldo = new JLabel("R$ 0,00");
        painel.add(criarCard("Saldo Atual",    lblSaldo,         COR_VERDE));

        lblTotalReceitas = new JLabel("R$ 0,00");
        painel.add(criarCard("Total Receitas", lblTotalReceitas, COR_VERDE));

        lblTotalDespesas = new JLabel("R$ 0,00");
        painel.add(criarCard("Total Despesas", lblTotalDespesas, COR_VERMELHO));

        return painel;
    }

    private JPanel criarCard(String titulo, JLabel valorLabel, Color corValor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(20, 24, 20, 24)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_CARD_LBL);
        lblTitulo.setForeground(COR_MUTED);

        valorLabel.setFont(FONTE_CARD_VAL);
        valorLabel.setForeground(corValor);
        valorLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        card.add(lblTitulo,  BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(COR_CARD);
        cabecalho.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel lblTitulo = new JLabel("Ultimas Movimentacoes  (duplo clique para editar)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COR_TEXTO);

        JButton btnAtualizar = criarBotao("Atualizar", COR_AZUL_CLARO);
        btnAtualizar.addActionListener(e -> atualizarDashboard());

        cabecalho.add(lblTitulo,    BorderLayout.WEST);
        cabecalho.add(btnAtualizar, BorderLayout.EAST);

        String[] colunas = {"Data", "Descricao", "Categoria", "Tipo", "Valor"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(tableModel);
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(36);
        tabela.setBackground(COR_CARD);
        tabela.setForeground(COR_TEXTO);
        tabela.setSelectionBackground(new Color(219, 234, 254));
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setGridColor(COR_BORDA);
        tabela.setShowGrid(true);
        tabela.setFocusable(false);
        tabela.setIntercellSpacing(new Dimension(0, 1));

        tabela.getTableHeader().setFont(FONTE_HEADER);
        tabela.getTableHeader().setBackground(new Color(241, 245, 249));
        tabela.getTableHeader().setForeground(COR_TEXTO);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tabela.getTableHeader().setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(120);

        tabela.getColumnModel().getColumn(3).setCellRenderer(new TipoRenderer());
        tabela.getColumnModel().getColumn(4).setCellRenderer(new ValorRenderer());

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linha = tabela.getSelectedRow();
                    if (linha >= 0 && linha < listaAtual.size()) {
                        abrirFormularioEdicao(listaAtual.get(linha));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBackground(COR_CARD);
        scroll.getViewport().setBackground(COR_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(scroll,    BorderLayout.CENTER);
        return painel;
    }

    public void atualizarDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                BigDecimal saldo    = transacaoController.calcularSaldoAtual();
                BigDecimal receitas = transacaoController.totalReceitas();
                BigDecimal despesas = transacaoController.totalDespesas();

                lblSaldo.setText(CurrencyUtil.formatar(saldo));
                lblSaldo.setForeground(saldo.compareTo(BigDecimal.ZERO) >= 0 ? COR_VERDE : COR_VERMELHO);
                lblTotalReceitas.setText(CurrencyUtil.formatar(receitas));
                lblTotalDespesas.setText(CurrencyUtil.formatar(despesas));

                listaAtual = transacaoController.listarUltimas(50);
                tableModel.setRowCount(0);
                for (Transacao t : listaAtual) {
                    tableModel.addRow(new Object[]{
                            t.getData().format(FMT_DATA),
                            t.getDescricao(),
                            t.getCategoria() != null ? t.getCategoria().getNome() : "-",
                            t.getTipo(),
                            CurrencyUtil.formatar(t.getValor())
                    });
                }
            } catch (Exception ex) {
                mostrarErro("Erro ao atualizar painel: " + ex.getMessage());
            }
        });
    }

    private void abrirFormularioNovaTransacao(TipoTransacao tipoInicial) {
        TransactionFormView form = new TransactionFormView(this, null, tipoInicial);
        form.setVisible(true);
        atualizarDashboard();
    }

    private void abrirFormularioEdicao(Transacao transacao) {
        TransactionFormView form = new TransactionFormView(this, transacao, transacao.getTipo());
        form.setVisible(true);
        atualizarDashboard();
    }

    private void abrirCategorias() {
        CategoryView view = new CategoryView(this);
        view.setVisible(true);
        atualizarDashboard();
    }

    private void abrirRelatorios() {
        ReportView view = new ReportView(this);
        view.setVisible(true);
    }

    private void abrirCalculadora() {
        CompoundInterestCalculatorView view = new CompoundInterestCalculatorView(this);
        view.setVisible(true);
    }

    private void abrirGraficoMensal(TipoTransacao tipo) {
        MonthlyChartView view = new MonthlyChartView(this, tipo);
        view.setVisible(true);
    }

    private JButton criarBotao(String texto, Color fundo) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(fundo);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void encerrarAplicacao() {
        int r = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Confirmar saida",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            HibernateUtil.shutdown();
            System.exit(0);
        }
    }

    private static class TipoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (val instanceof TipoTransacao tipo) {
                if (tipo == TipoTransacao.RECEITA) {
                    setForeground(new Color(34, 197, 94));
                    setText("Receita");
                } else {
                    setForeground(new Color(220, 60, 60));
                    setText("Despesa");
                }
            }
            setBackground(sel ? new Color(219, 234, 254) : Color.WHITE);
            return this;
        }
    }

    private static class ValorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            setHorizontalAlignment(RIGHT);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(new Color(15, 23, 42));
            setBackground(sel ? new Color(219, 234, 254) : Color.WHITE);
            return this;
        }
    }
}
