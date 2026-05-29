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
import java.util.concurrent.ExecutionException;

/**
 * Dialogo de formulario para cadastro e edicao de transacoes financeiras.
 * O combo de categorias e filtrado automaticamente pelo TipoTransacao selecionado.
 */
public class TransactionFormView extends JDialog {

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
    private static final Color COR_AZUL     = new Color(59, 130, 246);
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
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_BTN   = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Formato usado para compor o nome dos arquivos de backup.
     */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final NumberFormat FMT_VALOR = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
    /**
     * Quantidade maxima de digitos em centavos permitida.
     */
    private static final int MAX_DIGITOS_CENTAVOS = 15;

    static {
        FMT_VALOR.setMinimumFractionDigits(2);
        FMT_VALOR.setMaximumFractionDigits(2);
    }

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField           txtDescricao;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField           txtValor;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField           txtData;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JComboBox<TipoTransacao> cmbTipo;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JComboBox<Categoria>     cmbCategoria;

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final Transacao            transacaoParaEditar;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final TipoTransacao        tipoInicial;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final TransacaoController  transacaoController;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final CategoriaController  categoriaController;
    /**
     * Valor monetario armazenado em centavos.
     */
    private long                       valorCentavos;
    /**
     * Indica se o campo esta sendo atualizado internamente.
     */
    private boolean                    atualizandoValor;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private boolean                    preenchendoFormulario;

    /**
     * Cria uma nova instancia de TransactionFormView.
     *
     * @param owner janela proprietaria do dialogo
     * @param transacaoParaEditar parametro transacaoParaEditar
     * @param tipoInicial parametro tipoInicial
     */
    public TransactionFormView(Frame owner, Transacao transacaoParaEditar, TipoTransacao tipoInicial) {
        super(owner, transacaoParaEditar == null ? "Nova Transacao" : "Editar Transacao", true);
        AppIconUtil.aplicar(this);
        this.transacaoParaEditar = transacaoParaEditar;
        this.tipoInicial         = tipoInicial != null ? tipoInicial : TipoTransacao.DESPESA;
        this.transacaoController = new TransacaoController();
        this.categoriaController = new CategoriaController();
        construirInterface();
        if (transacaoParaEditar != null) preencherFormulario();
    }

    /**
     * Monta os componentes visuais da tela.
     */
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
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
        cmbTipo.addActionListener(e -> {
            if (!preenchendoFormulario) {
                recarregarCategorias();
            }
        });
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
        if (transacaoParaEditar == null) {
            recarregarCategorias();
        }
        p.add(cmbCategoria, gbc);

        return p;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
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
        recarregarCategorias(null);
    }

    /**
     * Executa a rotina recarregarCategorias.
     *
     * @param categoriaParaSelecionar parametro categoriaParaSelecionar
     */
    private void recarregarCategorias(Categoria categoriaParaSelecionar) {
        TipoTransacao tipoSelecionado = (TipoTransacao) cmbTipo.getSelectedItem();
        new SwingWorker<List<Categoria>, Void>() {
            /**
             * Executa a rotina doInBackground.
             *
             * @return resultado da operacao
             */
            @Override
            protected List<Categoria> doInBackground() {
                return tipoSelecionado != null
                        ? categoriaController.listarPorTipo(tipoSelecionado)
                        : List.of();
            }

            /**
             * Executa a rotina done.
             */
            @Override
            protected void done() {
                try {
                    cmbCategoria.removeAllItems();
                    cmbCategoria.addItem(null); // opcao "sem categoria"
                    List<Categoria> cats = get();
                    cats.forEach(cmbCategoria::addItem);
                    selecionarCategoria(categoriaParaSelecionar);
                } catch (Exception e) {
                    mostrarErro("Erro ao carregar categorias: " + mensagemErroWorker(e));
                }
            }
        }.execute();
    }

    /**
     * Executa a rotina preencherFormulario.
     */
    private void preencherFormulario() {
        preenchendoFormulario = true;
        try {
            cmbTipo.setSelectedItem(transacaoParaEditar.getTipo());
            txtDescricao.setText(transacaoParaEditar.getDescricao());
            definirValorCampo(transacaoParaEditar.getValor());
            txtData.setText(transacaoParaEditar.getData().format(FMT));
        } finally {
            preenchendoFormulario = false;
        }
        recarregarCategorias(transacaoParaEditar.getCategoria());
    }

    /**
     * Executa a rotina salvarTransacao.
     */
    private void salvarTransacao() {
        try {
            String        descricao = txtDescricao.getText().trim();
            BigDecimal    valor     = CurrencyUtil.parsear(txtValor.getText());
            LocalDate     data      = LocalDate.parse(txtData.getText().trim(), FMT);
            validarAnoNaoFuturo(data);
            TipoTransacao tipo      = (TipoTransacao) cmbTipo.getSelectedItem();
            Categoria     categoria = (Categoria) cmbCategoria.getSelectedItem();

            new SwingWorker<Void, Void>() {
                /**
                 * Executa a rotina doInBackground.
                 *
                 * @return resultado da operacao
                 */
                @Override
                protected Void doInBackground() {
                    if (transacaoParaEditar == null) {
                        Transacao nova = new Transacao(descricao, valor, data, tipo, categoria);
                        transacaoController.salvar(nova);
                    } else {
                        transacaoParaEditar.setDescricao(descricao);
                        transacaoParaEditar.setValor(valor);
                        transacaoParaEditar.setData(data);
                        transacaoParaEditar.setTipo(tipo);
                        transacaoParaEditar.setCategoria(categoria);
                        transacaoController.atualizar(transacaoParaEditar);
                    }
                    return null;
                }

                /**
                 * Executa a rotina done.
                 */
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TransactionFormView.this,
                                transacaoParaEditar == null
                                        ? "Transacao salva com sucesso!"
                                        : "Transacao atualizada com sucesso!",
                                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (Exception e) {
                        mostrarErro(mensagemErroWorker(e));
                    }
                }
            }.execute();

        } catch (DateTimeParseException e) {
            mostrarErro("Data invalida. Use o formato dd/MM/yyyy.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro inesperado: " + e.getMessage());
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    /**
     * Executa a rotina excluirTransacao.
     */
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

        new SwingWorker<Void, Void>() {
            /**
             * Executa a rotina doInBackground.
             *
             * @return resultado da operacao
             */
            @Override
            protected Void doInBackground() {
                transacaoController.excluir(transacaoParaEditar.getId());
                return null;
            }

            /**
             * Executa a rotina done.
             */
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TransactionFormView.this, "Transacao excluida com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception e) {
                    mostrarErro(mensagemErroWorker(e));
                }
            }
        }.execute();
    }

    /**
     * Adiciona o componente configurado ao painel informado.
     *
     * @param p parametro p
     * @param gbc parametro gbc
     * @param texto parametro texto
     */
    private void adicionarLabel(JPanel p, GridBagConstraints gbc, String texto) {
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_MUTED);
        p.add(lbl, gbc);
        gbc.insets = new Insets(0, 0, 14, 0);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param toolTip parametro toolTip
     * @return campo de texto configurado
     */
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

    /**
     * Aplica a aparencia padrao ao componente informado.
     *
     * @param cmb parametro cmb
     */
    private <E> void estilizarComboBox(JComboBox<E> cmb) {
        cmb.setFont(FONTE_INPUT);
        cmb.setForeground(COR_TEXTO);
        cmb.setBackground(COR_INPUT);
        cmb.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        cmb.setPreferredSize(new Dimension(0, 36));
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param cor parametro cor
     * @return botao configurado
     */
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

    /**
     * Exibe uma mensagem para o usuario.
     *
     * @param msg parametro msg
     */
    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro de Validacao",
                JOptionPane.ERROR_MESSAGE);
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
        return "Nao foi possivel concluir a operacao.";
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
     * Executa a rotina selecionarCategoria.
     *
     * @param categoriaParaSelecionar parametro categoriaParaSelecionar
     */
    private void selecionarCategoria(Categoria categoriaParaSelecionar) {
        if (categoriaParaSelecionar == null) {
            return;
        }
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            Categoria c = cmbCategoria.getItemAt(i);
            if (c != null && c.getId().equals(categoriaParaSelecionar.getId())) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Executa a rotina aplicarMascaraMoeda.
     *
     * @param campo parametro campo
     */
    private void aplicarMascaraMoeda(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new ValorMoedaDocumentFilter());
        campo.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * Executa a rotina validarAnoNaoFuturo.
     *
     * @param data parametro data
     */
    private void validarAnoNaoFuturo(LocalDate data) {
        int anoAtual = LocalDate.now().getYear();
        if (data.getYear() > anoAtual) {
            throw new IllegalArgumentException("O ano da data nao pode ser superior ao ano atual (" + anoAtual + ").");
        }
    }

    /**
     * Executa a rotina definirValorCampo.
     *
     * @param valor parametro valor
     */
    private void definirValorCampo(BigDecimal valor) {
        BigDecimal valorSeguro = valor != null ? valor : BigDecimal.ZERO;
        try {
            valorCentavos = valorSeguro.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (ArithmeticException e) {
            valorCentavos = 0L;
            mostrarErro("Valor monetario invalido.");
        }
        atualizarTextoValor();
    }

    /**
     * Executa a rotina atualizarTextoValor.
     */
    private void atualizarTextoValor() {
        atualizandoValor = true;
        txtValor.setText(FMT_VALOR.format(BigDecimal.valueOf(valorCentavos, 2)));
        atualizandoValor = false;
    }

    /**
     * Tipo responsavel por funcionalidades de ValorMoedaDocumentFilter.
     */
    private class ValorMoedaDocumentFilter extends DocumentFilter {
        /**
         * Insere texto aplicando a formatacao do documento.
         *
         * @param fb objeto de acesso ao documento filtrado
         * @param offset posicao inicial da alteracao
         * @param string texto a ser inserido
         * @param attr atributos do texto inserido
         * @throws BadLocationException se ocorrer erro ao alterar o documento
         */
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (atualizandoValor) {
                fb.insertString(offset, string, attr);
                return;
            }
            processarEntrada(fb, string);
        }

        /**
         * Substitui texto aplicando a formatacao do documento.
         *
         * @param fb objeto de acesso ao documento filtrado
         * @param offset posicao inicial da alteracao
         * @param length quantidade de caracteres afetados
         * @param text texto usado na substituicao
         * @param attrs atributos do texto substituido
         * @throws BadLocationException se ocorrer erro ao alterar o documento
         */
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (atualizandoValor) {
                fb.replace(offset, length, text, attrs);
                return;
            }
            boolean substituiTudo = offset == 0 && length == fb.getDocument().getLength();
            if (substituiTudo) {
                valorCentavos = 0L;
            }
            processarEntrada(fb, text);
        }

        /**
         * Remove texto mantendo a formatacao do documento.
         *
         * @param fb objeto de acesso ao documento filtrado
         * @param offset posicao inicial da alteracao
         * @param length quantidade de caracteres afetados
         * @throws BadLocationException se ocorrer erro ao alterar o documento
         */
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (atualizandoValor) {
                fb.remove(offset, length);
                return;
            }
            if (offset == 0 && length == fb.getDocument().getLength()) {
                valorCentavos = 0L;
                substituirTexto(fb);
                return;
            }
            valorCentavos /= 10;
            substituirTexto(fb);
        }

        /**
         * Executa a rotina processarEntrada.
         *
         * @param fb objeto de acesso ao documento filtrado
         * @param texto parametro texto
         * @throws BadLocationException se ocorrer erro ao alterar o documento
         */
        private void processarEntrada(FilterBypass fb, String texto) throws BadLocationException {
            if (atualizandoValor || texto == null) {
                return;
            }
            for (char ch : texto.toCharArray()) {
                if (Character.isDigit(ch)) {
                    if (Long.toString(valorCentavos).length() >= MAX_DIGITOS_CENTAVOS) {
                        continue;
                    }
                    valorCentavos = (valorCentavos * 10) + Character.getNumericValue(ch);
                }
            }
            substituirTexto(fb);
        }

        /**
         * Executa a rotina substituirTexto.
         *
         * @param fb objeto de acesso ao documento filtrado
         * @throws BadLocationException se ocorrer erro ao alterar o documento
         */
        private void substituirTexto(FilterBypass fb) throws BadLocationException {
            atualizandoValor = true;
            fb.replace(0, fb.getDocument().getLength(),
                    FMT_VALOR.format(BigDecimal.valueOf(valorCentavos, 2)), null);
            atualizandoValor = false;
        }
    }
}
