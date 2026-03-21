package com.sistema.view;

import com.sistema.controller.CategoriaController;
import com.sistema.controller.TransacaoController;
import com.sistema.model.Categoria;
import com.sistema.model.TipoTransacao;
import com.sistema.model.Transacao;
import com.sistema.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Diálogo de formulário para cadastro e edição de transações financeiras.
 * Suporta modo de criação (transacao == null) e modo de edição.
 */
public class TransactionFormView extends JDialog {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color COR_FUNDO   = new Color(15, 23, 42);
    private static final Color COR_CARD    = new Color(30, 41, 59);
    private static final Color COR_BORDA   = new Color(51, 65, 85);
    private static final Color COR_VERDE   = new Color(34, 197, 94);
    private static final Color COR_AZUL    = new Color(59, 130, 246);
    private static final Color COR_TEXTO   = new Color(241, 245, 249);
    private static final Color COR_MUTED   = new Color(148, 163, 184);
    private static final Color COR_INPUT   = new Color(15, 23, 42);
    private static final Color COR_CANCELAR = new Color(71, 85, 105);

    private static final Font FONTE_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_INPUT  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BTN    = new Font("Segoe UI", Font.BOLD, 14);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Componentes ───────────────────────────────────────────────────────────
    private JTextField txtDescricao;
    private JTextField txtValor;
    private JTextField txtData;
    private JComboBox<TipoTransacao> cmbTipo;
    private JComboBox<Categoria>     cmbCategoria;

    // ── Estado ────────────────────────────────────────────────────────────────
    private final Transacao transacaoParaEditar;
    private final TransacaoController transacaoController;
    private final CategoriaController categoriaController;

    // ── Construtor ────────────────────────────────────────────────────────────

    public TransactionFormView(Frame owner, Transacao transacaoParaEditar) {
        super(owner, transacaoParaEditar == null
                ? "Nova Transação" : "Editar Transação", true);
        this.transacaoParaEditar  = transacaoParaEditar;
        this.transacaoController  = new TransacaoController();
        this.categoriaController  = new CategoriaController();
        construirInterface();
        if (transacaoParaEditar != null) preencherFormulario();
    }

    // ── Construção ────────────────────────────────────────────────────────────

    private void construirInterface() {
        setSize(480, 480);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarFormulario(), BorderLayout.CENTER);
        add(criarRodape(),    BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COR_CARD);
        p.setBorder(new EmptyBorder(20, 24, 16, 24));

        String icone = transacaoParaEditar == null ? "➕" : "✏️";
        String titulo = transacaoParaEditar == null ? "Nova Transação" : "Editar Transação";

        JLabel lbl = new JLabel(icone + "  " + titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(COR_TEXTO);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(24, 24, 12, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(0, 0, 14, 0);
        gbc.weightx   = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Descrição
        adicionarLabel(p, gbc, "Descrição *");
        txtDescricao = criarTextField("Ex.: Supermercado, Salário...");
        p.add(txtDescricao, gbc);

        // Valor
        adicionarLabel(p, gbc, "Valor (R$) *");
        txtValor = criarTextField("0,00");
        p.add(txtValor, gbc);

        // Data
        adicionarLabel(p, gbc, "Data *");
        txtData = criarTextField(LocalDate.now().format(FMT));
        p.add(txtData, gbc);

        // Tipo - linha dividida
        adicionarLabel(p, gbc, "Tipo *");
        cmbTipo = new JComboBox<>(TipoTransacao.values());
        estilizarComboBox(cmbTipo);
        p.add(cmbTipo, gbc);

        // Categoria
        adicionarLabel(p, gbc, "Categoria");
        cmbCategoria = new JComboBox<>();
        estilizarComboBox(cmbCategoria);
        carregarCategorias();
        p.add(cmbCategoria, gbc);

        return p;
    }

    private JPanel criarRodape() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(0, 24, 24, 24));

        JButton btnCancelar = criarBotao("Cancelar", COR_CANCELAR);
        JButton btnSalvar   = criarBotao(
                transacaoParaEditar == null ? "Salvar" : "Atualizar", COR_AZUL);

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e   -> salvarTransacao());

        p.add(btnCancelar);
        p.add(btnSalvar);
        return p;
    }

    // ── Lógica ───────────────────────────────────────────────────────────────

    private void carregarCategorias() {
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem(null); // opção "sem categoria"
        List<Categoria> cats = categoriaController.listarTodos();
        for (Categoria c : cats) cmbCategoria.addItem(c);
    }

    private void preencherFormulario() {
        txtDescricao.setText(transacaoParaEditar.getDescricao());
        txtValor.setText(transacaoParaEditar.getValor().toPlainString().replace(".", ","));
        txtData.setText(transacaoParaEditar.getData().format(FMT));
        cmbTipo.setSelectedItem(transacaoParaEditar.getTipo());
        if (transacaoParaEditar.getCategoria() != null) {
            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                Categoria c = cmbCategoria.getItemAt(i);
                if (c != null && c.getId().equals(transacaoParaEditar.getCategoria().getId())) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void salvarTransacao() {
        try {
            String descricao = txtDescricao.getText().trim();
            BigDecimal valor = CurrencyUtil.parsear(txtValor.getText());
            LocalDate data   = LocalDate.parse(txtData.getText().trim(), FMT);
            TipoTransacao tipo       = (TipoTransacao) cmbTipo.getSelectedItem();
            Categoria     categoria  = (Categoria) cmbCategoria.getSelectedItem();

            if (transacaoParaEditar == null) {
                Transacao nova = new Transacao(descricao, valor, data, tipo, categoria);
                transacaoController.salvar(nova);
                JOptionPane.showMessageDialog(this,
                        "✅ Transação salva com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                transacaoParaEditar.setDescricao(descricao);
                transacaoParaEditar.setValor(valor);
                transacaoParaEditar.setData(data);
                transacaoParaEditar.setTipo(tipo);
                transacaoParaEditar.setCategoria(categoria);
                transacaoController.atualizar(transacaoParaEditar);
                JOptionPane.showMessageDialog(this,
                        "✅ Transação atualizada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();

        } catch (DateTimeParseException e) {
            mostrarErro("Data inválida. Use o formato dd/MM/yyyy.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private void adicionarLabel(JPanel p, GridBagConstraints gbc, String texto) {
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        p.add(lbl, gbc);
        gbc.insets = new Insets(0, 0, 14, 0);
    }

    private JTextField criarTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONTE_INPUT);
        tf.setForeground(COR_TEXTO);
        tf.setBackground(COR_INPUT);
        tf.setCaretColor(COR_TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(8, 12, 8, 12)));
        tf.setToolTipText(placeholder);
        return tf;
    }

    private <E> void estilizarComboBox(JComboBox<E> cmb) {
        cmb.setFont(FONTE_INPUT);
        cmb.setForeground(COR_TEXTO);
        cmb.setBackground(COR_INPUT);
        cmb.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        cmb.setPreferredSize(new Dimension(0, 36));
    }

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
        JOptionPane.showMessageDialog(this, msg, "Erro de Validação",
                JOptionPane.ERROR_MESSAGE);
    }
}