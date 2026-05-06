package com.sistema.view;

import com.sistema.controller.CategoriaController;
import com.sistema.controller.TransacaoController;
import com.sistema.model.Categoria;
import com.sistema.model.TipoTransacao;
import com.sistema.model.Transacao;
import com.sistema.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Dialogo de formulario para cadastro e edicao de transacoes financeiras.
 * O combo de categorias e filtrado automaticamente pelo TipoTransacao selecionado.
 */
public class TransactionFormView extends JDialog {

    private static final Color COR_FUNDO    = new Color(248, 250, 252);
    private static final Color COR_CARD     = new Color(255, 255, 255);
    private static final Color COR_BORDA    = new Color(203, 213, 225);
    private static final Color COR_VERDE    = new Color(34, 197, 94);
    private static final Color COR_VERMELHO = new Color(220, 60, 60);
    private static final Color COR_AZUL     = new Color(59, 130, 246);
    private static final Color COR_GRAFITE  = new Color(100, 116, 139);
    private static final Color COR_TEXTO    = new Color(15, 23, 42);
    private static final Color COR_MUTED    = new Color(71, 85, 105);
    private static final Color COR_INPUT    = new Color(255, 255, 255);

    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONTE_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BTN   = new Font("Segoe UI", Font.BOLD, 14);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FMT_VALOR = NumberFormat.getNumberInstance(new Locale("pt", "BR"));

    static {
        FMT_VALOR.setMinimumFractionDigits(2);
        FMT_VALOR.setMaximumFractionDigits(2);
    }

    private JTextField           txtDescricao;
    private JTextField           txtValor;
    private JTextField           txtData;
    private JComboBox<TipoTransacao> cmbTipo;
    private JComboBox<Categoria>     cmbCategoria;

    private final Transacao            transacaoParaEditar;
    private final TipoTransacao        tipoInicial;
    private final TransacaoController  transacaoController;
    private final CategoriaController  categoriaController;
    private long                       valorCentavos;
    private boolean                    atualizandoValor;

    public TransactionFormView(Frame owner, Transacao transacaoParaEditar, TipoTransacao tipoInicial) {
        super(owner, transacaoParaEditar == null ? "Nova Transacao" : "Editar Transacao", true);
        this.transacaoParaEditar = transacaoParaEditar;
        this.tipoInicial         = tipoInicial != null ? tipoInicial : TipoTransacao.DESPESA;
        this.transacaoController = new TransacaoController();
        this.categoriaController = new CategoriaController();
        construirInterface();
        if (transacaoParaEditar != null) preencherFormulario();
    }

    private void construirInterface() {
        setSize(480, 500);
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

        String titulo = transacaoParaEditar == null ? "Nova Transacao" : "Editar Transacao";
        JLabel lbl = new JLabel(titulo);
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

        // Tipo — acima de tudo para filtrar categorias
        adicionarLabel(p, gbc, "Tipo");
        cmbTipo = new JComboBox<>(TipoTransacao.values());
        cmbTipo.setSelectedItem(tipoInicial);
        estilizarComboBox(cmbTipo);
        // Ao mudar o tipo, recarrega o combo de categorias
        cmbTipo.addActionListener(e -> recarregarCategorias());
        p.add(cmbTipo, gbc);

        // Descricao
        adicionarLabel(p, gbc, "Descricao");
        txtDescricao = criarTextField("Ex.: Supermercado, Salario...");
        p.add(txtDescricao, gbc);

        // Valor
        adicionarLabel(p, gbc, "Valor (R$)");
        txtValor = criarTextField("0,00");
        aplicarMascaraMoeda(txtValor);
        definirValorCampo(BigDecimal.ZERO);
        p.add(txtValor, gbc);

        // Data
        adicionarLabel(p, gbc, "Data (dd/MM/yyyy)");
        txtData = criarTextField(LocalDate.now().format(FMT));
        aplicarMascaraData(txtData);
        p.add(txtData, gbc);

        // Categoria — filtrada pelo tipo selecionado
        adicionarLabel(p, gbc, "Categoria");
        cmbCategoria = new JComboBox<>();
        estilizarComboBox(cmbCategoria);
        recarregarCategorias();
        p.add(cmbCategoria, gbc);

        return p;
    }

    private JPanel criarRodape() {
        int colunas = transacaoParaEditar == null ? 2 : 3;
        JPanel p = new JPanel(new GridLayout(1, colunas, 12, 0));
        p.setBackground(COR_FUNDO);
        p.setBorder(new EmptyBorder(0, 24, 24, 24));

        JButton btnCancelar = criarBotao("Cancelar", COR_GRAFITE);
        JButton btnSalvar   = criarBotao(
                transacaoParaEditar == null ? "Salvar" : "Atualizar", COR_AZUL);

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e   -> salvarTransacao());

        if (transacaoParaEditar != null) {
            JButton btnExcluir = criarBotao("Excluir", COR_VERMELHO);
            btnExcluir.addActionListener(e -> excluirTransacao());
            p.add(btnExcluir);
        }
        p.add(btnCancelar);
        p.add(btnSalvar);
        return p;
    }

    // ── Logica ────────────────────────────────────────────────────────────────

    /** Recarrega o combo de categorias filtrando pelo tipo atualmente selecionado. */
    private void recarregarCategorias() {
        TipoTransacao tipoSelecionado = (TipoTransacao) cmbTipo.getSelectedItem();
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem(null); // opcao "sem categoria"

        if (tipoSelecionado != null) {
            List<Categoria> cats = categoriaController.listarPorTipo(tipoSelecionado);
            cats.forEach(cmbCategoria::addItem);
        }
    }

    private void preencherFormulario() {
        cmbTipo.setSelectedItem(transacaoParaEditar.getTipo());
        recarregarCategorias(); // garante que a lista esta correta antes de selecionar
        txtDescricao.setText(transacaoParaEditar.getDescricao());
        definirValorCampo(transacaoParaEditar.getValor());
        txtData.setText(transacaoParaEditar.getData().format(FMT));

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
            String        descricao = txtDescricao.getText().trim();
            BigDecimal    valor     = CurrencyUtil.parsear(txtValor.getText());
            LocalDate     data      = LocalDate.parse(txtData.getText().trim(), FMT);
            validarAnoNaoFuturo(data);
            TipoTransacao tipo      = (TipoTransacao) cmbTipo.getSelectedItem();
            Categoria     categoria = (Categoria) cmbCategoria.getSelectedItem();

            if (transacaoParaEditar == null) {
                Transacao nova = new Transacao(descricao, valor, data, tipo, categoria);
                transacaoController.salvar(nova);
                JOptionPane.showMessageDialog(this, "Transacao salva com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                transacaoParaEditar.setDescricao(descricao);
                transacaoParaEditar.setValor(valor);
                transacaoParaEditar.setData(data);
                transacaoParaEditar.setTipo(tipo);
                transacaoParaEditar.setCategoria(categoria);
                transacaoController.atualizar(transacaoParaEditar);
                JOptionPane.showMessageDialog(this, "Transacao atualizada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();

        } catch (DateTimeParseException e) {
            mostrarErro("Data invalida. Use o formato dd/MM/yyyy.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private void excluirTransacao() {
        if (transacaoParaEditar == null || transacaoParaEditar.getId() == null) {
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Excluir a transacao \"" + transacaoParaEditar.getDescricao() + "\"?",
                "Confirmar exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            transacaoController.excluir(transacaoParaEditar.getId());
            JOptionPane.showMessageDialog(this, "Transacao excluida com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado ao excluir: " + e.getMessage());
        }
    }

    private void adicionarLabel(JPanel p, GridBagConstraints gbc, String texto) {
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        p.add(lbl, gbc);
        gbc.insets = new Insets(0, 0, 14, 0);
    }

    private JTextField criarTextField(String toolTip) {
        JTextField tf = new JTextField();
        tf.setFont(FONTE_INPUT);
        tf.setForeground(COR_TEXTO);
        tf.setBackground(COR_INPUT);
        tf.setCaretColor(COR_TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                new EmptyBorder(8, 12, 8, 12)));
        tf.setToolTipText(toolTip);
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
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setBorder(new EmptyBorder(10, 0, 10, 0));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro de Validacao",
                JOptionPane.ERROR_MESSAGE);
    }

    private void aplicarMascaraData(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DateDocumentFilter());
    }

    private void aplicarMascaraMoeda(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new ValorMoedaDocumentFilter());
        campo.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void validarAnoNaoFuturo(LocalDate data) {
        int anoAtual = LocalDate.now().getYear();
        if (data.getYear() > anoAtual) {
            throw new IllegalArgumentException("O ano da data nao pode ser superior ao ano atual (" + anoAtual + ").");
        }
    }

    private void definirValorCampo(BigDecimal valor) {
        BigDecimal valorSeguro = valor != null ? valor : BigDecimal.ZERO;
        valorCentavos = valorSeguro.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValue();
        atualizarTextoValor();
    }

    private void atualizarTextoValor() {
        atualizandoValor = true;
        txtValor.setText(FMT_VALOR.format(BigDecimal.valueOf(valorCentavos, 2)));
        atualizandoValor = false;
    }

    private class ValorMoedaDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (atualizandoValor) {
                fb.insertString(offset, string, attr);
                return;
            }
            processarEntrada(fb, string);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (atualizandoValor) {
                fb.replace(offset, length, text, attrs);
                return;
            }
            processarEntrada(fb, text);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (atualizandoValor) {
                fb.remove(offset, length);
                return;
            }
            valorCentavos /= 10;
            substituirTexto(fb);
        }

        private void processarEntrada(FilterBypass fb, String texto) throws BadLocationException {
            if (atualizandoValor || texto == null) {
                return;
            }
            for (char ch : texto.toCharArray()) {
                if (Character.isDigit(ch)) {
                    valorCentavos = (valorCentavos * 10) + Character.getNumericValue(ch);
                }
            }
            substituirTexto(fb);
        }

        private void substituirTexto(FilterBypass fb) throws BadLocationException {
            atualizandoValor = true;
            fb.replace(0, fb.getDocument().getLength(),
                    FMT_VALOR.format(BigDecimal.valueOf(valorCentavos, 2)), null);
            atualizandoValor = false;
        }
    }
}
