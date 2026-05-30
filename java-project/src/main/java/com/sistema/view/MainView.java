package com.sistema.view;

import com.sistema.controller.TransacaoController;
import com.sistema.model.Transacao;
import com.sistema.model.TipoTransacao;
import com.sistema.util.CurrencyUtil;
import com.sistema.util.HibernateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Janela principal da aplicacao financeira.
 */
public class MainView extends JFrame {

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_FUNDO       = new Color(248, 250, 252);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_CARD        = new Color(255, 255, 255);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_BORDA       = new Color(203, 213, 225);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_VERDE       = new Color(34, 197, 94);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_VERMELHO    = new Color(220, 60, 60);
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
    private static final Color COR_GRAFITE     = new Color(100, 116, 139);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_TEXTO       = new Color(15, 23, 42);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_MUTED       = new Color(71, 85, 105);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_TITULO   = new Font("Segoe UI", Font.BOLD, 22);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_CARD_VAL = new Font("Segoe UI", Font.BOLD, 26);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_CARD_LBL = new Font("Segoe UI", Font.PLAIN, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_TABELA   = new Font("Segoe UI", Font.PLAIN, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_HEADER   = new Font("Segoe UI", Font.BOLD, 13);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Font FONTE_BTN      = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel lblSaldo;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel lblTotalReceitas;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JLabel lblTotalDespesas;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private DefaultTableModel tableModel;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTable tabela;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private List<Transacao> listaAtual = List.of();
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JTextField txtBusca;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JComboBox<String> cmbFiltroPeriodo;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JComboBox<String> cmbMesFiltro;
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private JSpinner spnAnoFiltro;

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private final TransacaoController transacaoController;

    /**
     * Cria uma nova instancia de MainView.
     */
    public MainView() {
        this.transacaoController = new TransacaoController();
        configurarJanela();
        construirInterface();
        atualizarDashboard();
    }

    /**
     * Executa a rotina configurarJanela.
     */
    private void configurarJanela() {
        setTitle("Sistema Financeiro Pessoal");
        AppIconUtil.aplicar(this);
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

    /**
     * Monta os componentes visuais da tela.
     */
    private void construirInterface() {
        setLayout(new BorderLayout());
        add(criarPainelTopo(),    BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titulo = new JLabel("Painel Financeiro");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnNovo = criarBotaoMenu("Novo", UiColors.COR_NOVO, UiIcons.plus(Color.WHITE));
        JPopupMenu menuNovo = criarMenuSuspenso();
        menuNovo.add(criarItemMenu("Nova Receita", COR_VERDE, UiIcons.plus(COR_VERDE),
                () -> abrirFormularioNovaTransacao(TipoTransacao.RECEITA)));
        menuNovo.add(criarItemMenu("Nova Despesa", COR_VERMELHO, UiIcons.plus(COR_VERMELHO),
                () -> abrirFormularioNovaTransacao(TipoTransacao.DESPESA)));
        btnNovo.addActionListener(e -> exibirMenu(btnNovo, menuNovo));

        JButton btnGraficos = criarBotaoMenu("Gr\u00e1ficos", UiColors.COR_GRAFICOS, UiIcons.chart(Color.WHITE));
        JPopupMenu menuGraficos = criarMenuSuspenso();
        menuGraficos.add(criarItemMenu("Despesa Mensal", COR_VERMELHO, UiIcons.chart(COR_VERMELHO),
                () -> abrirGraficoMensal(TipoTransacao.DESPESA)));
        menuGraficos.add(criarItemMenu("Receita Mensal", COR_VERDE, UiIcons.chart(COR_VERDE),
                () -> abrirGraficoMensal(TipoTransacao.RECEITA)));
        btnGraficos.addActionListener(e -> exibirMenu(btnGraficos, menuGraficos));

        JButton btnCats    = criarBotao("Categorias", UiColors.COR_CATEGORIAS, UiIcons.category(Color.WHITE));
        JButton btnRelat   = criarBotao("Relat\u00f3rios", UiColors.COR_RELATORIOS, UiIcons.report(Color.WHITE));
        JButton btnCalc    = criarBotao("Calculadoras", UiColors.COR_CALCULADORAS, UiIcons.calculator(Color.WHITE));

        btnCats.addActionListener(e  -> abrirCategorias());
        btnRelat.addActionListener(e -> abrirRelatorios());
        btnCalc.addActionListener(e  -> abrirCalculadora());

        btnPanel.add(btnGraficos);
        btnPanel.add(btnCalc);
        btnPanel.add(btnRelat);
        btnPanel.add(btnCats);
        btnPanel.add(btnNovo);

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

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarPainelCentral() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 24, 20, 24));
        painel.add(criarPainelCards(),  BorderLayout.NORTH);
        painel.add(criarPainelTabela(), BorderLayout.CENTER);
        return painel;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @param titulo parametro titulo
     * @param valorLabel parametro valorLabel
     * @param corValor parametro corValor
     * @return painel configurado
     */
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

    /**
     * Cria e configura o componente solicitado.
     *
     * @return painel configurado
     */
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_CARD);
        painel.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(COR_CARD);
        cabecalho.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel lblTitulo = new JLabel("Movimentacoes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COR_TEXTO);

        JButton btnAtualizar = criarBotao("Atualizar", COR_AZUL_CLARO, UiIcons.refresh(Color.WHITE));
        btnAtualizar.addActionListener(e -> atualizarDashboard());

        cabecalho.add(lblTitulo, BorderLayout.WEST);
        cabecalho.add(criarPainelFiltroTabela(btnAtualizar), BorderLayout.EAST);

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
        ((DefaultTableCellRenderer) tabela.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(120);

        DefaultTableCellRenderer centroRenderer = new DefaultTableCellRenderer();
        centroRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centroRenderer);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centroRenderer);
        tabela.getColumnModel().getColumn(2).setCellRenderer(centroRenderer);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new TipoRenderer());
        tabela.getColumnModel().getColumn(4).setCellRenderer(new ValorRenderer());

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Executa a rotina mouseClicked.
             *
             * @param e evento recebido pela interface
             */
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

        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setBackground(COR_CARD);
        tabelaWrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        tabelaWrapper.add(scroll, BorderLayout.CENTER);

        painel.add(cabecalho,     BorderLayout.NORTH);
        painel.add(tabelaWrapper, BorderLayout.CENTER);
        return painel;
    }

    /**
     * Cria os controles de filtro da tabela principal.
     *
     * @param btnAtualizar botao de atualizacao manual
     * @return painel com filtros de periodo
     */
    private JPanel criarPainelFiltroTabela(JButton btnAtualizar) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painel.setOpaque(false);

        txtBusca = new JTextField();
        txtBusca.setFont(FONTE_TABELA);
        txtBusca.setForeground(COR_TEXTO);
        txtBusca.setBackground(Color.WHITE);
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                new EmptyBorder(7, 10, 7, 10)));
        txtBusca.setPreferredSize(new Dimension(210, 34));
        txtBusca.setToolTipText("Pesquisar por data, descricao, categoria, tipo ou valor");
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarDashboard(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarDashboard(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarDashboard(); }
        });

        cmbFiltroPeriodo = new JComboBox<>(new String[]{
                "Todas",
                "Ultimos 30 dias",
                "Ultimos 60 dias",
                "Ultimos 90 dias",
                "Mes especifico",
                "Ano especifico"
        });
        estilizarComboFiltro(cmbFiltroPeriodo, 150);

        cmbMesFiltro = new JComboBox<>(new String[]{
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        });
        cmbMesFiltro.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        estilizarComboFiltro(cmbMesFiltro, 120);

        spnAnoFiltro = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        spnAnoFiltro.setFont(FONTE_TABELA);
        spnAnoFiltro.setPreferredSize(new Dimension(78, 34));
        JSpinner.NumberEditor editorAno = new JSpinner.NumberEditor(spnAnoFiltro, "0");
        editorAno.getFormat().setGroupingUsed(false);
        spnAnoFiltro.setEditor(editorAno);

        cmbFiltroPeriodo.addActionListener(e -> {
            atualizarVisibilidadeFiltrosPeriodo();
            atualizarDashboard();
        });
        cmbMesFiltro.addActionListener(e -> atualizarDashboard());
        spnAnoFiltro.addChangeListener(e -> atualizarDashboard());

        painel.add(new JLabel("Buscar:"));
        painel.add(txtBusca);
        painel.add(new JLabel("Periodo:"));
        painel.add(cmbFiltroPeriodo);
        painel.add(cmbMesFiltro);
        painel.add(spnAnoFiltro);
        painel.add(btnAtualizar);

        atualizarVisibilidadeFiltrosPeriodo();
        return painel;
    }

    /**
     * Atualiza os controles visiveis conforme o filtro selecionado.
     */
    private void atualizarVisibilidadeFiltrosPeriodo() {
        if (cmbFiltroPeriodo == null || cmbMesFiltro == null || spnAnoFiltro == null) {
            return;
        }
        String filtro = (String) cmbFiltroPeriodo.getSelectedItem();
        boolean mesEspecifico = "Mes especifico".equals(filtro);
        boolean anoEspecifico = "Ano especifico".equals(filtro);
        cmbMesFiltro.setVisible(mesEspecifico);
        spnAnoFiltro.setVisible(mesEspecifico || anoEspecifico);
    }

    /**
     * Executa a rotina atualizarDashboard.
     */
    public void atualizarDashboard() {
        PeriodoTabela filtroTabela = obterFiltroTabela();
        String termoBusca = obterTermoBusca();
        new SwingWorker<DashboardData, Void>() {
            /**
             * Executa a rotina doInBackground.
             *
             * @return resultado da operacao
             */
            @Override
            protected DashboardData doInBackground() {
                List<Transacao> transacoes = filtrarTransacoesPorBusca(
                        carregarTransacoesTabela(filtroTabela), termoBusca);
                BigDecimal receitas = somarPorTipo(transacoes, TipoTransacao.RECEITA);
                BigDecimal despesas = somarPorTipo(transacoes, TipoTransacao.DESPESA);
                BigDecimal saldo = receitas.subtract(despesas);
                return new DashboardData(saldo, receitas, despesas, transacoes);
            }

            /**
             * Executa a rotina done.
             */
            @Override
            protected void done() {
                try {
                    DashboardData dados = get();
                    lblSaldo.setText(CurrencyUtil.formatar(dados.saldo()));
                    lblSaldo.setForeground(dados.saldo().compareTo(BigDecimal.ZERO) >= 0 ? COR_VERDE : COR_VERMELHO);
                    lblTotalReceitas.setText(CurrencyUtil.formatar(dados.receitas()));
                    lblTotalDespesas.setText(CurrencyUtil.formatar(dados.despesas()));

                    listaAtual = dados.transacoes();
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
                    mostrarErro("Erro ao atualizar painel: " + mensagemErroWorker(ex));
                }
            }
        }.execute();
    }

    /**
     * Soma as movimentacoes de um tipo dentro da lista exibida.
     *
     * @param transacoes movimentacoes exibidas na tabela
     * @param tipo tipo financeiro somado
     * @return total do tipo informado
     */
    private BigDecimal somarPorTipo(List<Transacao> transacoes, TipoTransacao tipo) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == tipo)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtem o texto digitado no campo de busca da tabela.
     *
     * @return termo de busca informado
     */
    private String obterTermoBusca() {
        return txtBusca != null ? txtBusca.getText() : "";
    }

    /**
     * Filtra as movimentacoes pelos campos exibidos na tabela.
     *
     * @param transacoes movimentacoes carregadas pelo filtro de periodo
     * @param termoBusca texto pesquisado pelo usuario
     * @return lista compativel com o termo pesquisado
     */
    private List<Transacao> filtrarTransacoesPorBusca(List<Transacao> transacoes, String termoBusca) {
        String termo = normalizarBusca(termoBusca);
        if (termo.isBlank()) {
            return transacoes;
        }

        return transacoes.stream()
                .filter(t -> textoPesquisaTransacao(t).contains(termo))
                .toList();
    }

    /**
     * Monta o texto pesquisavel de uma movimentacao.
     *
     * @param transacao movimentacao avaliada
     * @return texto normalizado para busca
     */
    private String textoPesquisaTransacao(Transacao transacao) {
        String categoria = transacao.getCategoria() != null ? transacao.getCategoria().getNome() : "-";
        String tipo = transacao.getTipo() == TipoTransacao.RECEITA ? "Receita" : "Despesa";
        String valorFormatado = CurrencyUtil.formatar(transacao.getValor());
        String valorSimples = transacao.getValor() != null ? transacao.getValor().toPlainString() : "";

        return normalizarBusca(String.join(" ",
                transacao.getData().format(FMT_DATA),
                transacao.getDescricao(),
                categoria,
                tipo,
                valorFormatado,
                valorSimples));
    }

    /**
     * Normaliza textos para busca sem diferenciar acentos, maiusculas e alguns separadores.
     *
     * @param texto texto original
     * @return texto preparado para comparacao
     */
    private String normalizarBusca(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento
                .toLowerCase()
                .replace("r$", " ")
                .replaceAll("[.,/\\-]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Obtem o filtro atual da tabela a partir dos controles visuais.
     *
     * @return filtro selecionado
     */
    private PeriodoTabela obterFiltroTabela() {
        if (cmbFiltroPeriodo == null) {
            return new PeriodoTabela("Todas", 1, LocalDate.now().getYear());
        }
        String filtro = (String) cmbFiltroPeriodo.getSelectedItem();
        int mes = cmbMesFiltro != null ? cmbMesFiltro.getSelectedIndex() + 1 : LocalDate.now().getMonthValue();
        int ano = spnAnoFiltro != null ? (Integer) spnAnoFiltro.getValue() : LocalDate.now().getYear();
        return new PeriodoTabela(filtro != null ? filtro : "Todas", mes, ano);
    }

    /**
     * Carrega as movimentacoes conforme o filtro selecionado na tabela.
     *
     * @param filtroTabela filtro selecionado na tela
     * @return lista de transacoes filtradas
     */
    private List<Transacao> carregarTransacoesTabela(PeriodoTabela filtroTabela) {
        String filtro = filtroTabela.tipo();
        LocalDate hoje = LocalDate.now();

        if ("Ultimos 30 dias".equals(filtro)) {
            return transacaoController.filtrarPorPeriodo(hoje.minusDays(30), hoje);
        }
        if ("Ultimos 60 dias".equals(filtro)) {
            return transacaoController.filtrarPorPeriodo(hoje.minusDays(60), hoje);
        }
        if ("Ultimos 90 dias".equals(filtro)) {
            return transacaoController.filtrarPorPeriodo(hoje.minusDays(90), hoje);
        }
        if ("Mes especifico".equals(filtro)) {
            YearMonth periodo = YearMonth.of(filtroTabela.ano(), filtroTabela.mes());
            return transacaoController.filtrarPorPeriodo(periodo.atDay(1), periodo.atEndOfMonth());
        }
        if ("Ano especifico".equals(filtro)) {
            return transacaoController.filtrarPorPeriodo(
                    LocalDate.of(filtroTabela.ano(), 1, 1),
                    LocalDate.of(filtroTabela.ano(), 12, 31));
        }

        return transacaoController.listarTodos();
    }

    /**
     * Abre a janela informada para o usuario.
     *
     * @param tipoInicial parametro tipoInicial
     */
    private void abrirFormularioNovaTransacao(TipoTransacao tipoInicial) {
        TransactionFormView form = new TransactionFormView(this, null, tipoInicial);
        form.setVisible(true);
        atualizarDashboard();
    }

    /**
     * Abre a janela informada para o usuario.
     *
     * @param transacao parametro transacao
     */
    private void abrirFormularioEdicao(Transacao transacao) {
        TransactionFormView form = new TransactionFormView(this, transacao, transacao.getTipo());
        form.setVisible(true);
        atualizarDashboard();
    }

    /**
     * Abre a janela informada para o usuario.
     */
    private void abrirCategorias() {
        CategoryView view = new CategoryView(this);
        view.setVisible(true);
        atualizarDashboard();
    }

    /**
     * Abre a janela informada para o usuario.
     */
    private void abrirRelatorios() {
        ReportView view = new ReportView(this);
        view.setVisible(true);
    }

    /**
     * Abre a janela informada para o usuario.
     */
    private void abrirCalculadora() {
        CalculatorMenuView view = new CalculatorMenuView(this);
        view.setVisible(true);
    }

    /**
     * Abre a janela informada para o usuario.
     *
     * @param tipo parametro tipo
     */
    private void abrirGraficoMensal(TipoTransacao tipo) {
        MonthlyChartView view = new MonthlyChartView(this, tipo);
        view.setVisible(true);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param fundo parametro fundo
     * @return botao configurado
     */
    private JButton criarBotao(String texto, Color fundo) {
        return criarBotao(texto, fundo, null);
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param fundo parametro fundo
     * @param icone parametro icone
     * @return botao configurado
     */
    private JButton criarBotao(String texto, Color fundo, Icon icone) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(fundo);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setIcon(icone);
        btn.setIconTextGap(8);
        btn.setPreferredSize(new Dimension(Math.max(128, btn.getPreferredSize().width), 38));
        return btn;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param fundo parametro fundo
     * @param icone parametro icone
     * @return botao configurado
     */
    private JButton criarBotaoMenu(String texto, Color fundo, Icon icone) {
        JButton btn = criarBotao(texto, fundo, icone);
        btn.setToolTipText("Abrir op\u00e7\u00f5es de " + texto);
        return btn;
    }

    /**
     * Aplica estilo aos combos de filtro da tabela.
     *
     * @param combo combo a ser estilizado
     * @param largura largura preferencial
     */
    private void estilizarComboFiltro(JComboBox<String> combo, int largura) {
        combo.setFont(FONTE_TABELA);
        combo.setForeground(COR_TEXTO);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        combo.setPreferredSize(new Dimension(largura, 34));
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @return resultado da operacao
     */
    private JPopupMenu criarMenuSuspenso() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        return menu;
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param texto parametro texto
     * @param cor parametro cor
     * @param icone parametro icone
     * @param acao parametro acao
     * @return resultado da operacao
     */
    private JMenuItem criarItemMenu(String texto, Color cor, Icon icone, Runnable acao) {
        JMenuItem item = new JMenuItem(texto, icone);
        item.setFont(FONTE_BTN);
        item.setForeground(COR_TEXTO);
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(8, 12, 8, 18));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.addActionListener(e -> acao.run());
        item.setIconTextGap(8);
        item.setOpaque(true);
        item.addChangeListener(e -> item.setForeground(item.isArmed() ? cor : COR_TEXTO));
        return item;
    }

    /**
     * Executa a rotina exibirMenu.
     *
     * @param origem parametro origem
     * @param menu parametro menu
     */
    private void exibirMenu(JButton origem, JPopupMenu menu) {
        menu.show(origem, 0, origem.getHeight() + 4);
    }

    /**
     * Exibe uma mensagem para o usuario.
     *
     * @param msg parametro msg
     */
    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
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
     * Tipo responsavel por funcionalidades de DashboardData.
     */
    private record DashboardData(BigDecimal saldo, BigDecimal receitas,
                                 BigDecimal despesas, List<Transacao> transacoes) {}

    /**
     * Filtro de periodo aplicado na tabela principal.
     */
    private record PeriodoTabela(String tipo, int mes, int ano) {}

    /**
     * Executa a rotina encerrarAplicacao.
     */
    private void encerrarAplicacao() {
        int r = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Confirmar saida",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            HibernateUtil.shutdown();
            System.exit(0);
        }
    }

    /**
     * Tipo responsavel por funcionalidades de TipoRenderer.
     */
    private static class TipoRenderer extends DefaultTableCellRenderer {
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

    /**
     * Tipo responsavel por funcionalidades de ValorRenderer.
     */
    private static class ValorRenderer extends DefaultTableCellRenderer {
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
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(new Color(15, 23, 42));
            setBackground(sel ? new Color(219, 234, 254) : Color.WHITE);
            return this;
        }
    }
}
