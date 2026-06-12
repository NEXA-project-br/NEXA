package com.sistema.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Utilitario para acoplar um seletor de calendario a campos de data.
 */
final class DatePickerUtil {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MES_ANO =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR"));

    private static final Color COR_AZUL = new Color(59, 130, 246);
    private static final Color COR_BORDA = new Color(203, 213, 225);
    private static final Color COR_FUNDO = new Color(255, 255, 255);
    private static final Color COR_TEXTO = new Color(15, 23, 42);
    private static final Color COR_MUTED = new Color(71, 85, 105);

    private DatePickerUtil() {
    }

    /**
     * Cria uma linha contendo o campo de data e o botao que abre o calendario.
     *
     * @param campo campo de data no formato dd/MM/yyyy
     * @return painel pronto para ser adicionado na tela
     */
    static JPanel criarCampoComCalendario(JTextField campo) {
        JPanel painel = new JPanel(new BorderLayout(6, 0));
        painel.setOpaque(false);

        JButton botao = new JButton("...");
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setForeground(Color.WHITE);
        botao.setBackground(COR_AZUL);
        botao.setBorder(new EmptyBorder(8, 10, 8, 10));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setToolTipText("Abrir calendário");
        botao.addActionListener(e -> abrirCalendario(campo, botao));

        painel.add(campo, BorderLayout.CENTER);
        painel.add(botao, BorderLayout.EAST);
        return painel;
    }

    private static void abrirCalendario(JTextField campo, JButton origem) {
        LocalDate dataInicial = lerData(campo);
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        popup.add(new CalendarioPanel(campo, popup, YearMonth.from(dataInicial), dataInicial));
        popup.show(origem, 0, origem.getHeight());
    }

    private static LocalDate lerData(JTextField campo) {
        try {
            return LocalDate.parse(campo.getText().trim(), FMT);
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    private static class CalendarioPanel extends JPanel {

        private final JTextField campo;
        private final JPopupMenu popup;
        private final LocalDate dataSelecionada;
        private YearMonth mesAtual;

        private CalendarioPanel(JTextField campo, JPopupMenu popup,
                                YearMonth mesAtual, LocalDate dataSelecionada) {
            this.campo = campo;
            this.popup = popup;
            this.mesAtual = mesAtual;
            this.dataSelecionada = dataSelecionada;
            setBackground(COR_FUNDO);
            setBorder(new EmptyBorder(10, 10, 10, 10));
            montar();
        }

        private void montar() {
            removeAll();
            setLayout(new BorderLayout(0, 8));
            add(criarCabecalho(), BorderLayout.NORTH);
            add(criarDias(), BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        private JPanel criarCabecalho() {
            JPanel painel = new JPanel(new BorderLayout(8, 0));
            painel.setOpaque(false);

            JButton anterior = criarBotaoNavegacao("<");
            JButton proximo = criarBotaoNavegacao(">");
            JLabel titulo = new JLabel(capitalizar(mesAtual.format(MES_ANO)), SwingConstants.CENTER);
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
            titulo.setForeground(COR_TEXTO);

            anterior.addActionListener(e -> {
                mesAtual = mesAtual.minusMonths(1);
                montar();
            });
            proximo.addActionListener(e -> {
                mesAtual = mesAtual.plusMonths(1);
                montar();
            });

            painel.add(anterior, BorderLayout.WEST);
            painel.add(titulo, BorderLayout.CENTER);
            painel.add(proximo, BorderLayout.EAST);
            return painel;
        }

        private JPanel criarDias() {
            JPanel grid = new JPanel(new GridLayout(0, 7, 4, 4));
            grid.setOpaque(false);

            String[] dias = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
            for (String dia : dias) {
                JLabel label = new JLabel(dia, SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setForeground(COR_MUTED);
                grid.add(label);
            }

            LocalDate primeiroDia = mesAtual.atDay(1);
            int deslocamento = primeiroDia.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
            for (int i = 0; i < deslocamento; i++) {
                grid.add(new JLabel(""));
            }

            for (int dia = 1; dia <= mesAtual.lengthOfMonth(); dia++) {
                LocalDate data = mesAtual.atDay(dia);
                JButton botaoDia = criarBotaoDia(data);
                grid.add(botaoDia);
            }

            return grid;
        }

        private JButton criarBotaoDia(LocalDate data) {
            JButton botao = new JButton(String.valueOf(data.getDayOfMonth()));
            botao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            botao.setFocusPainted(false);
            botao.setBorder(BorderFactory.createLineBorder(COR_BORDA));
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            boolean selecionado = data.equals(dataSelecionada);
            boolean hoje = data.equals(LocalDate.now());
            if (selecionado || hoje) {
                botao.setForeground(Color.WHITE);
                botao.setBackground(COR_AZUL);
                botao.setOpaque(true);
            } else {
                botao.setForeground(COR_TEXTO);
                botao.setBackground(COR_FUNDO);
            }

            botao.addActionListener(e -> {
                campo.setText(data.format(FMT));
                popup.setVisible(false);
            });
            return botao;
        }

        private JButton criarBotaoNavegacao(String texto) {
            JButton botao = new JButton(texto);
            botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
            botao.setForeground(COR_TEXTO);
            botao.setBackground(new Color(241, 245, 249));
            botao.setBorder(BorderFactory.createLineBorder(COR_BORDA));
            botao.setFocusPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return botao;
        }

        private String capitalizar(String texto) {
            if (texto == null || texto.isBlank()) {
                return "";
            }
            return texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
        }
    }
}
