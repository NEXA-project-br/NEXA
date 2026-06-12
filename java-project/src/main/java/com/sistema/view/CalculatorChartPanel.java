package com.sistema.view;

import com.sistema.util.CurrencyUtil;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel responsavel por desenhar graficos das calculadoras.
 */
class CalculatorChartPanel extends JPanel {

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_BORDA = new Color(203, 213, 225);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_GRID = new Color(226, 232, 240);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_TEXTO = new Color(15, 23, 42);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_MUTED = new Color(71, 85, 105);
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private static final Color COR_AZUL = new Color(59, 130, 246);

    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private List<BigDecimal> valores = List.of();
    /**
     * Atributo usado pelo funcionamento desta classe.
     */
    private String titulo = "Evolução";

    CalculatorChartPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        setPreferredSize(new Dimension(520, 260));
    }

    void atualizarDados(String titulo, List<BigDecimal> valores) {
        this.titulo = titulo;
        this.valores = valores == null ? List.of() : new ArrayList<>(valores);
        repaint();
    }

    void limpar() {
        atualizarDados("Evolução", List.of());
    }

    /**
     * Executa a rotina paintComponent.
     *
     * @param g contexto grafico usado no desenho
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int largura = getWidth();
        int altura = getHeight();
        int margemEsquerda = 76;
        int margemDireita = 24;
        int margemTopo = 42;
        int margemBase = 48;
        int areaW = largura - margemEsquerda - margemDireita;
        int areaH = altura - margemTopo - margemBase;

        desenharTitulo(g2, largura);
        desenharEixos(g2, margemEsquerda, margemTopo, areaW, areaH);

        if (valores.size() < 2) {
            desenharVazio(g2, largura, altura);
            g2.dispose();
            return;
        }

        BigDecimal maximo = valores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
        if (maximo.compareTo(BigDecimal.ZERO) <= 0) {
            maximo = BigDecimal.ONE;
        }

        desenharEscala(g2, margemEsquerda, margemTopo, areaH, maximo);
        desenharLinha(g2, margemEsquerda, margemTopo, areaW, areaH, maximo);
        desenharRodape(g2, margemEsquerda, margemTopo, areaW, areaH);

        g2.dispose();
    }

    /**
     * Executa a rotina desenharTitulo.
     *
     * @param g2 parametro g2
     * @param largura parametro largura
     */
    private void desenharTitulo(Graphics2D g2, int largura) {
        g2.setColor(COR_TEXTO);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        int textoW = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, Math.max(16, (largura - textoW) / 2), 24);
    }

    /**
     * Executa a rotina desenharEixos.
     *
     * @param g2 parametro g2
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param w parametro w
     * @param h parametro h
     */
    private void desenharEixos(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(COR_GRID);
        for (int i = 0; i <= 4; i++) {
            int linhaY = y + (h * i / 4);
            g2.drawLine(x, linhaY, x + w, linhaY);
        }
        g2.setColor(COR_BORDA);
        g2.drawLine(x, y + h, x + w, y + h);
        g2.drawLine(x, y, x, y + h);
    }

    /**
     * Executa a rotina desenharVazio.
     *
     * @param g2 parametro g2
     * @param largura parametro largura
     * @param altura parametro altura
     */
    private void desenharVazio(Graphics2D g2, int largura, int altura) {
        String texto = "Preencha os campos e calcule para visualizar o grafico.";
        g2.setColor(COR_MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        int textoW = g2.getFontMetrics().stringWidth(texto);
        g2.drawString(texto, Math.max(16, (largura - textoW) / 2), altura / 2);
    }

    /**
     * Executa a rotina desenharEscala.
     *
     * @param g2 parametro g2
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param h parametro h
     * @param maximo parametro maximo
     */
    private void desenharEscala(Graphics2D g2, int x, int y, int h, BigDecimal maximo) {
        g2.setColor(COR_MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        for (int i = 0; i <= 4; i++) {
            BigDecimal valor = maximo.multiply(BigDecimal.valueOf(4 - i))
                    .divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
            String label = abreviarMoeda(valor);
            int labelW = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x - labelW - 8, y + (h * i / 4) + 4);
        }
    }

    /**
     * Executa a rotina desenharLinha.
     *
     * @param g2 parametro g2
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param w parametro w
     * @param h parametro h
     * @param maximo parametro maximo
     */
    private void desenharLinha(Graphics2D g2, int x, int y, int w, int h, BigDecimal maximo) {
        g2.setColor(COR_AZUL);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int pontoAnteriorX = x;
        int pontoAnteriorY = y + h;
        for (int i = 0; i < valores.size(); i++) {
            double proporcaoX = valores.size() == 1 ? 0 : (double) i / (valores.size() - 1);
            double proporcaoY = valores.get(i).doubleValue() / maximo.doubleValue();
            int px = x + (int) Math.round(proporcaoX * w);
            int py = y + h - (int) Math.round(Math.max(0, proporcaoY) * h);
            if (i > 0) {
                g2.drawLine(pontoAnteriorX, pontoAnteriorY, px, py);
            }
            pontoAnteriorX = px;
            pontoAnteriorY = py;
        }

        BigDecimal ultimo = valores.get(valores.size() - 1);
        g2.fillOval(pontoAnteriorX - 4, pontoAnteriorY - 4, 8, 8);
        g2.setColor(COR_TEXTO);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString(CurrencyUtil.formatar(ultimo), Math.max(x, pontoAnteriorX - 96), Math.max(38, pontoAnteriorY - 10));
    }

    /**
     * Executa a rotina desenharRodape.
     *
     * @param g2 parametro g2
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param w parametro w
     * @param h parametro h
     */
    private void desenharRodape(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(COR_MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString("Mes 0", x, y + h + 22);
        String fim = "Mes " + (valores.size() - 1);
        int fimW = g2.getFontMetrics().stringWidth(fim);
        g2.drawString(fim, x + w - fimW, y + h + 22);
    }

    /**
     * Executa a rotina abreviarMoeda.
     *
     * @param valor parametro valor
     * @return texto formatado
     */
    private String abreviarMoeda(BigDecimal valor) {
        BigDecimal milhao = BigDecimal.valueOf(1_000_000);
        BigDecimal mil = BigDecimal.valueOf(1_000);
        if (valor.compareTo(milhao) >= 0) {
            return "R$ " + valor.divide(milhao, 1, RoundingMode.HALF_UP).toPlainString().replace(".", ",") + " mi";
        }
        if (valor.compareTo(mil) >= 0) {
            return "R$ " + valor.divide(mil, 0, RoundingMode.HALF_UP).toPlainString() + " mil";
        }
        return CurrencyUtil.formatar(valor);
    }
}
