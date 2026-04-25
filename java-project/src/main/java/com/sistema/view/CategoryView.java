package com.sistema.view;

import com.sistema.controller.CategoriaController;
import com.sistema.model.Categoria;
import com.sistema.model.TipoTransacao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Dialogo para gerenciamento de categorias financeiras.
 * Cada categoria e criada com um TipoTransacao (RECEITA ou DESPESA).
 */
public class CategoryView extends JDialog {

    private static final Color COR_FUNDO    = new Color(248, 250, 252);
    private static final Color COR_CARD     = new Color(255, 255, 255);
    private static final Color COR_BORDA    = new Color(203, 213, 225);
    private static final Color COR_VERDE    = new Color(34, 197, 94);
    private static final Color COR_VERMELHO = new Color(220, 60, 60);
    private static final Color COR_GRAFITE  = new Color(100, 116, 139);
    private static final Color COR_TEXTO    = new Color(15, 23, 42);
    private static final Color COR_MUTED    = new Color(71, 85, 105);
    private static final Color COR_INPUT    = new Color(255, 255, 255);
    private static final Color COR_SELECAO  = new Color(219, 234, 254);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_ITEM   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_BTN    = new Font("Segoe UI", Font.BOLD, 13);

    private DefaultListModel<Categoria> listModel;
    private JList<Categoria>            listaCategorias;
    private JTextField                  txtNome;
    private JComboBox<TipoTransacao>    cmbTipo;

    private final CategoriaController categoriaController;

    public CategoryView(Frame owner) {
        super(owner, "Gerenciamento de Categorias", true);
        this.categoriaController = new CategoriaController();
        construirInterface();
        carregarCategorias();
    }

    private void construirInterface() {
        setSize(480, 600);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpo(),     BorderLayout.CENTER);
        add(criarRodape(),    BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COR_CARD);
        p.setBorder(new EmptyBorder(20, 24, 16, 24));
        JLabel lbl = new JLabel("Categorias");
        lbl.setFont(FONTE_TITULO);
        lbl.setForeground(COR_TEXTO);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JPanel criarCorpo() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(20, 24, 0, 24));

        // ── Secao de adicao ───────────────────────────────────────────────────
        JPanel addPanel = new JPanel(new BorderLayout(0, 8));
        addPanel.setBackground(COR_FUNDO);

        JLabel lblSec = new JLabel("Nova categoria");
        lblSec.setFont(FONTE_LABEL);
        lblSec.setForeground(COR_MUTED);

        // Linha: nome + tipo
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(COR_FUNDO);

        txtNome = new JTextField();
        txtNome.setFont(FONTE_ITEM);
        txtNome.setForeground(COR_TEXTO);
        txtNome.setBackground(COR_INPUT);
        txtNome.setCaretColor(COR_TEXTO);
        txtNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(8, 12, 8, 12)));
        txtNome.setToolTipText("Nome da categoria");
        txtNome.addActionListener(e -> adicionarCategoria());

        cmbTipo = new JComboBox<>(TipoTransacao.values());
        cmbTipo.setFont(FONTE_ITEM);
        cmbTipo.setForeground(COR_TEXTO);
        cmbTipo.setBackground(COR_INPUT);
        cmbTipo.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        cmbTipo.setPreferredSize(new Dimension(120, 38));

        inputRow.add(txtNome, BorderLayout.CENTER);
        inputRow.add(cmbTipo, BorderLayout.EAST);

        JButton btnAdicionar = criarBotao("Adicionar", COR_VERDE);
        btnAdicionar.addActionListener(e -> adicionarCategoria());

        addPanel.add(lblSec,       BorderLayout.NORTH);
        addPanel.add(inputRow,     BorderLayout.CENTER);
        addPanel.add(btnAdicionar, BorderLayout.SOUTH);

        // ── Lista de categorias ───────────────────────────────────────────────
        JPanel listaPanel = new JPanel(new BorderLayout(0, 8));
        listaPanel.setBackground(COR_FUNDO);

        JLabel lblLista = new JLabel("Categorias cadastradas");
        lblLista.setFont(FONTE_LABEL);
        lblLista.setForeground(COR_MUTED);

        listModel = new DefaultListModel<>();
        listaCategorias = new JList<>(listModel);
        listaCategorias.setFont(FONTE_ITEM);
        listaCategorias.setForeground(COR_TEXTO);
        listaCategorias.setBackground(COR_CARD);
        listaCategorias.setSelectionBackground(COR_SELECAO);
        listaCategorias.setSelectionForeground(COR_TEXTO);
        listaCategorias.setFixedCellHeight(44);
        listaCategorias.setCellRenderer(new CategoriaRenderer());

        JScrollPane scroll = new JScrollPane(listaCategorias);
        scroll.getViewport().setBackground(COR_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));

        listaPanel.add(lblLista, BorderLayout.NORTH);
        listaPanel.add(scroll,   BorderLayout.CENTER);

        p.add(addPanel,   BorderLayout.NORTH);
        p.add(listaPanel, BorderLayout.CENTER);
        return p;
    }

    private JPanel criarRodape() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(16, 24, 24, 24));

        JButton btnExcluir = criarBotao("Excluir Selecionada", COR_VERMELHO);
        JButton btnFechar  = criarBotao("Fechar",              COR_GRAFITE);

        btnExcluir.addActionListener(e -> excluirCategoriaSelecionada());
        btnFechar.addActionListener(e  -> dispose());

        p.add(btnExcluir);
        p.add(btnFechar);
        return p;
    }

    // ── Logica ────────────────────────────────────────────────────────────────

    private void carregarCategorias() {
        listModel.clear();
        List<Categoria> cats = categoriaController.listarTodos();
        cats.forEach(listModel::addElement);
    }

    private void adicionarCategoria() {
        String nome = txtNome.getText().trim();
        TipoTransacao tipo = (TipoTransacao) cmbTipo.getSelectedItem();

        if (nome.isEmpty()) {
            mostrarAviso("Digite um nome para a categoria.");
            return;
        }
        try {
            categoriaController.salvarPorNome(nome, tipo);
            txtNome.setText("");
            carregarCategorias();
            JOptionPane.showMessageDialog(this,
                    "Categoria \"" + nome + "\" adicionada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void excluirCategoriaSelecionada() {
        Categoria selecionada = listaCategorias.getSelectedValue();
        if (selecionada == null) {
            mostrarAviso("Selecione uma categoria para excluir.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Excluir a categoria \"" + selecionada.getNome() + "\"?",
                "Confirmar exclusao", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            categoriaController.excluir(selecionada.getId());
            carregarCategorias();
        } catch (IllegalStateException e) {
            mostrarAviso(e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(10, 0, 10, 0));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    private void mostrarAviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atencao", JOptionPane.WARNING_MESSAGE);
    }

    // ── Renderer customizado ──────────────────────────────────────────────────

    private static class CategoriaRenderer extends DefaultListCellRenderer {
        private static final Color COR_VERDE    = new Color(34, 197, 94);
        private static final Color COR_VERMELHO = new Color(220, 60, 60);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Categoria c) {
                String badge = c.getTipo() == TipoTransacao.RECEITA ? "[R]" : "[D]";
                Color  cor   = c.getTipo() == TipoTransacao.RECEITA ? COR_VERDE : COR_VERMELHO;
                setText("<html><b><font color='" + toHex(cor) + "'>" + badge
                        + "</font></b>  " + c.getNome() + "</html>");
            }

            setBackground(isSelected ? new Color(219, 234, 254) : Color.WHITE);
            setForeground(new Color(15, 23, 42));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                    new EmptyBorder(0, 12, 0, 8)));
            return this;
        }

        private static String toHex(Color c) {
            return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        }
    }
}
