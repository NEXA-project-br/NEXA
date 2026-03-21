package com.sistema.view;

import com.sistema.controller.TransacaoController;
import com.sistema.model.Transacao;
import com.sistema.model.TipoTransacao;
import com.sistema.util.CurrencyUtil;
import com.sistema.util.HibernateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * View principal do sistema — Dashboard financeiro.
 * Exibe saldo atual, totais de receita/despesa e últimas movimentações.
 */
public class MainView extends JFrame {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(15, 23, 42);
    private static final Color COR_CARD         = new Color(30, 41, 59);
    private static final Color COR_BORDA        = new Color(51, 65, 85);
    private static final Color COR_VERDE        = new Color(34, 197, 94);
    private static final Color COR_VERMELHO     = new Color(239, 68, 68);
    private static final Color COR_AZUL         = new Color(59, 130, 246);
    private static final Color COR_TEXTO        = new Color(241, 245, 249);
    private static final Color COR_TEXTO_MUTED  = new Color(148, 163, 184);
    private static final Color COR_HEADER_TABLE = new Color(51, 65, 85);
    private static final Color COR_ROW_ALT      = new Color(30, 41, 59);

    private static final Font FONTE_TITULO   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONTE_CARD_VAL = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONTE_CARD_LBL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_TABELA   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_HEADER   = new Font("Segoe UI", Font.BOLD, 13);

    private static final DateTimeFormatter FMT_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Componentes dinâmicos ─────────────────────────────────────────────────
    private JLabel lblSaldo;
    private JLabel lblTotalReceitas;
    private JLabel lblTotalDespesas;
    private DefaultTableModel tableModel;
    private JTable tabela;

    private final TransacaoController transacaoController;

    // ── Construtor ────────────────────────────────────────────────────────────

    public MainView() {
        this.transacaoController = new TransacaoController();
        configurarJanela();
        construirInterface();
        atualizarDashboard();
    }

    // ── Configuração da janela ────────────────────────────────────────────────

    private void configurarJanela() {
        setTitle("💰 Sistema Financeiro Pessoal");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                encerrarAplicacao();
            }
        });
    }

    // ── Construção da interface ───────────────────────────────────────────────

    private void construirInterface() {
        setLayout(new BorderLayout());

        add(criarPainelTopo(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
    }

    /** Barra superior com título e botões de navegação */
    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Título
        JLabel titulo = new JLabel("💰 Painel Financeiro");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO);

        // Botões de ação
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnNovaTransacao = criarBotao("+ Nova Transação", COR_AZUL);
        JButton btnCategorias    = criarBotao("📂 Categorias",     COR_CARD);
        JButton btnRelatorios    = criarBotao("📊 Relatórios",     COR_CARD);

        // Estilo diferenciado nos botões secundários
        btnCategorias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(6, 14, 6, 14)));
        btnRelatorios.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(6, 14, 6, 14)));

        btnNovaTransacao.addActionListener(e -> abrirFormularioTransacao(null));
        btnCategorias.addActionListener(e -> abrirCategorias());
        btnRelatorios.addActionListener(e -> abrirRelatorios());

        btnPanel.add(btnRelatorios);
        btnPanel.add(btnCategorias);
        btnPanel.add(btnNovaTransacao);

        painel.add(titulo,   BorderLayout.WEST);
        painel.add(btnPanel, BorderLayout.EAST);

        // Linha separadora inferior
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COR_FUNDO);
        wrapper.add(painel, BorderLayout.CENTER);
        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        wrapper.add(sep, BorderLayout.SOUTH);
        return wrapper;
    }

    /** Área central com cards de resumo e tabela de movimentações */
    private JPanel criarPainelCentral() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 24, 20, 24));

        painel.add(criarPainelCards(),  BorderLayout.NORTH);
        painel.add(criarPainelTabela(), BorderLayout.CENTER);

        return painel;
    }

    /** Linha de cards: saldo, receitas, despesas */
    private JPanel criarPainelCards() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 16, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Card Saldo
        lblSaldo = new JLabel("R$ 0,00");
        painel.add(criarCard("💳 Saldo Atual", lblSaldo, COR_VERDE));

        // Card Receitas
        lblTotalReceitas = new JLabel("R$ 0,00");
        painel.add(criarCard("📈 Total Receitas", lblTotalReceitas, COR_VERDE));

        // Card Despesas
        lblTotalDespesas = new JLabel("R$ 0,00");
        painel.add(criarCard("📉 Total Despesas", lblTotalDespesas, COR_VERMELHO));

        return painel;
    }

    /** Cria um card individual de resumo financeiro */
    private JPanel criarCard(String titulo, JLabel valorLabel, Color corValor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(20, 24, 20, 24)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_CARD_LBL);
        lblTitulo.setForeground(COR_TEXTO_MUTED);

        valorLabel.setFont(FONTE_CARD_VAL);
        valorLabel.setForeground(corValor);
        valorLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        card.add(lblTitulo,  BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        return card;
    }

    /** Painel com tabela de últimas movimentações */
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(0, 0, 0, 0)));

        // Cabeçalho da seção
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(COR_CARD);
        cabecalho.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel lblTitulo = new JLabel("🕐 Últimas Movimentações");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COR_TEXTO);

        JButton btnAtualizar = new JButton("↻ Atualizar");
        btnAtualizar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAtualizar.setForeground(COR_TEXTO_MUTED);
        btnAtualizar.setBackground(COR_FUNDO);
        btnAtualizar.setBorder(new EmptyBorder(4, 10, 4, 10));
        btnAtualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.addActionListener(e -> atualizarDashboard());

        cabecalho.add(lblTitulo,    BorderLayout.WEST);
        cabecalho.add(btnAtualizar, BorderLayout.EAST);

        // Tabela
        String[] colunas = {"Data", "Descrição", "Categoria", "Tipo", "Valor"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(tableModel);
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(36);
        tabela.setBackground(COR_CARD);
        tabela.setForeground(COR_TEXTO);
        tabela.setSelectionBackground(new Color(51, 65, 85));
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setGridColor(COR_BORDA);
        tabela.setShowGrid(true);
        tabela.setFocusable(false);
        tabela.setIntercellSpacing(new Dimension(0, 1));

        // Cabeçalho da tabela
        tabela.getTableHeader().setFont(FONTE_HEADER);
        tabela.getTableHeader().setBackground(COR_HEADER_TABLE);
        tabela.getTableHeader().setForeground(COR_TEXTO_MUTED);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tabela.getTableHeader().setReorderingAllowed(false);

        // Larguras de coluna
        tabela.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Renderer para colorir a coluna Tipo e Valor
        tabela.getColumnModel().getColumn(3).setCellRenderer(new TipoRenderer());
        tabela.getColumnModel().getColumn(4).setCellRenderer(new ValorRenderer());

        // Duplo clique → editar transação
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarTransacaoSelecionada();
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

    // ── Atualização de dados ──────────────────────────────────────────────────

    /**
     * Recarrega todos os dados do dashboard a partir do banco.
     * Deve ser chamado após qualquer operação de escrita.
     */
    public void atualizarDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Saldo e totais
                BigDecimal saldo    = transacaoController.calcularSaldoAtual();
                BigDecimal receitas = transacaoController.totalReceitas();
                BigDecimal despesas = transacaoController.totalDespesas();

                lblSaldo.setText(CurrencyUtil.formatar(saldo));
                lblSaldo.setForeground(saldo.compareTo(BigDecimal.ZERO) >= 0
                        ? COR_VERDE : COR_VERMELHO);

                lblTotalReceitas.setText(CurrencyUtil.formatar(receitas));
                lblTotalDespesas.setText(CurrencyUtil.formatar(despesas));

                // Últimas 50 transações
                List<Transacao> lista = transacaoController.listarUltimas(50);
                tableModel.setRowCount(0);
                for (Transacao t : lista) {
                    tableModel.addRow(new Object[]{
                            t.getData().format(FMT_DATA),
                            t.getDescricao(),
                            t.getCategoria() != null ? t.getCategoria().getNome() : "—",
                            t.getTipo(),
                            CurrencyUtil.formatar(t.getValor())
                    });
                }
            } catch (Exception ex) {
                mostrarErro("Erro ao atualizar painel: " + ex.getMessage());
            }
        });
    }

    // ── Navegação ─────────────────────────────────────────────────────────────

    private void abrirFormularioTransacao(Transacao transacaoParaEditar) {
        TransactionFormView form = new TransactionFormView(this, transacaoParaEditar);
        form.setVisible(true);
        atualizarDashboard();
    }

    private void editarTransacaoSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        // Para edição, recarregamos pelo índice da lista (simplificado via listarUltimas)
        abrirFormularioTransacao(null);
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

    // ── Utilitários ───────────────────────────────────────────────────────────

    private JButton criarBotao(String texto, Color fundo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(COR_TEXTO);
        btn.setBackground(fundo);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void encerrarAplicacao() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Confirmar saída",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            HibernateUtil.shutdown();
            System.exit(0);
        }
    }

    // ── Renderers personalizados ──────────────────────────────────────────────

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
                    setText("▲ " + tipo.getDescricao());
                } else {
                    setForeground(new Color(239, 68, 68));
                    setText("▼ " + tipo.getDescricao());
                }
            }
            setBackground(sel ? new Color(51, 65, 85) : new Color(30, 41, 59));
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
            setForeground(new Color(241, 245, 249));
            setBackground(sel ? new Color(51, 65, 85) : new Color(30, 41, 59));
            return this;
        }
    }
}