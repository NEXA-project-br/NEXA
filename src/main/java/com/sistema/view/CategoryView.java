package com.sistema.view;

import com.sistema.controller.CategoriaController;
import com.sistema.model.Categoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Diálogo para gerenciamento de categorias financeiras.
 * Permite listar, adicionar e excluir categorias.
 */
public class CategoryView extends JDialog {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color COR_FUNDO    = new Color(15, 23, 42);
    private static final Color COR_CARD     = new Color(30, 41, 59);
    private static final Color COR_BORDA    = new Color(51, 65, 85);
    private static final Color COR_VERDE    = new Color(34, 197, 94);
    private static final Color COR_VERMELHO = new Color(239, 68, 68);
    private static final Color COR_TEXTO    = new Color(241, 245, 249);
    private static final Color COR_MUTED    = new Color(148, 163, 184);
    private static final Color COR_INPUT    = new Color(15, 23, 42);
    private static final Color COR_SELECAO  = new Color(51, 65, 85);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_ITEM   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_BTN    = new Font("Segoe UI", Font.BOLD, 13);

    // ── Componentes ───────────────────────────────────────────────────────────
    private DefaultListModel<Categoria> listModel;
    private JList<Categoria>            listaCategorias;
    private JTextField                  txtNome;

    private final CategoriaController categoriaController;

    // ── Construtor ────────────────────────────────────────────────────────────

    public CategoryView(Frame owner) {
        super(owner, "Gerenciamento de Categorias", true);
        this.categoriaController = new CategoriaController();
        construirInterface();
        carregarCategorias();
    }

    // ── Construção ────────────────────────────────────────────────────────────

    private void construirInterface() {
        setSize(450, 560);
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

        JLabel lbl = new JLabel("📂  Categorias");
        lbl.setFont(FONTE_TITULO);
        lbl.setForeground(COR_TEXTO);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JPanel criarCorpo() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(20, 24, 0, 24));

        // ── Seção de adição ──────────────────────────────────────────────────
        JPanel painelAdicao = new JPanel(new BorderLayout(8, 0));
        painelAdicao.setBackground(COR_FUNDO);

        JLabel lbl = new JLabel("Nova categoria:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COR_MUTED);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));

        txtNome = new JTextField();
        txtNome.setFont(FONTE_ITEM);
        txtNome.setForeground(COR_TEXTO);
        txtNome.setBackground(COR_INPUT);
        txtNome.setCaretColor(COR_TEXTO);
        txtNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(8, 12, 8, 12)));
        txtNome.addActionListener(e -> adicionarCategoria());

        JButton btnAdicionar = criarBotao("+ Adicionar", COR_VERDE);
        btnAdicionar.addActionListener(e -> adicionarCategoria());

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(COR_FUNDO);
        inputRow.add(txtNome,      BorderLayout.CENTER);
        inputRow.add(btnAdicionar, BorderLayout.EAST);

        JPanel addPanel = new JPanel(new BorderLayout());
        addPanel.setBackground(COR_FUNDO);
        addPanel.add(lbl,      BorderLayout.NORTH);
        addPanel.add(inputRow, BorderLayout.CENTER);

        // ── Lista de categorias ──────────────────────────────────────────────
        JLabel lblLista = new JLabel("Categorias cadastradas:");
        lblLista.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLista.setForeground(COR_MUTED);

        listModel = new DefaultListModel<>();
        listaCategorias = new JList<>(listModel);
        listaCategorias.setFont(FONTE_ITEM);
        listaCategorias.setForeground(COR_TEXTO);
        listaCategorias.setBackground(COR_CARD);
        listaCategorias.setSelectionBackground(COR_SELECAO);
        listaCategorias.setSelectionForeground(COR_TEXTO);
        listaCategorias.setFixedCellHeight(42);
        listaCategorias.setCellRenderer(new CategoriaRenderer());
        listaCategorias.setBorder(new EmptyBorder(4, 0, 4, 0));

        JScrollPane scroll = new JScrollPane(listaCategorias);
        scroll.setBackground(COR_CARD);
        scroll.getViewport().setBackground(COR_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));

        JPanel listaPanel = new JPanel(new BorderLayout(0, 8));
        listaPanel.setBackground(COR_FUNDO);
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

        JButton btnExcluir = criarBotao("🗑  Excluir Selecionada", COR_VERMELHO);
        JButton btnFechar  = criarBotao("Fechar", new Color(71, 85, 105));

        btnExcluir.addActionListener(e -> excluirCategoriaSelecionada());
        btnFechar.addActionListener(e  -> dispose());

        p.add(btnExcluir);
        p.add(btnFechar);
        return p;
    }

    // ── Lógica ───────────────────────────────────────────────────────────────

    private void carregarCategorias() {
        listModel.clear();
        List<Categoria> cats = categoriaController.listarTodos();
        cats.forEach(listModel::addElement);
    }

    private void adicionarCategoria() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            mostrarErro("Digite um nome para a categoria.");
            return;
        }
        try {
            categoriaController.salvarPorNome(nome);
            txtNome.setText("");
            carregarCategorias();
            JOptionPane.showMessageDialog(this,
                    "✅ Categoria \"" + nome + "\" adicionada!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void excluirCategoriaSelecionada() {
        Categoria selecionada = listaCategorias.getSelectedValue();
        if (selecionada == null) {
            mostrarErro("Selecione uma categoria para excluir.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Excluir a categoria \"" + selecionada.getNome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            categoriaController.excluir(selecionada.getId());
            carregarCategorias();
        } catch (IllegalStateException e) {
            mostrarErro(e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BTN);
        btn.setForeground(COR_TEXTO);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(10, 0, 10, 0));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    // ── Renderer customizado ──────────────────────────────────────────────────

    private static class CategoriaRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Categoria c) {
                setText("  📁  " + c.getNome());
            }

            setBackground(isSelected ? new Color(51, 65, 85) : new Color(30, 41, 59));
            setForeground(new Color(241, 245, 249));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 65, 85)),
                    new EmptyBorder(0, 8, 0, 8)));
            return this;
        }
    }
}