package com.sistema.view;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/**
 * Classe utilitaria que cria icones usados na interface.
 */
final class UiIcons {

    /**
     * Cria uma nova instancia de UiIcons.
     */
    private UiIcons() {}

    static Icon plus(Color color) {
        return icon(color, Shape.PLUS);
    }

    static Icon chart(Color color) {
        return icon(color, Shape.CHART);
    }

    static Icon report(Color color) {
        return icon(color, Shape.REPORT);
    }

    static Icon category(Color color) {
        return icon(color, Shape.CATEGORY);
    }

    static Icon calculator(Color color) {
        return icon(color, Shape.CALCULATOR);
    }

    static Icon refresh(Color color) {
        return icon(color, Shape.REFRESH);
    }

    static Icon pdf(Color color) {
        return icon(color, Shape.PDF);
    }

    static Icon close(Color color) {
        return icon(color, Shape.CLOSE);
    }

    /**
     * Executa a rotina icon.
     *
     * @param color parametro color
     * @param shape parametro shape
     * @return resultado da operacao
     */
    private static Icon icon(Color color, Shape shape) {
        return new LineIcon(color, shape);
    }

    /**
     * Tipo responsavel por funcionalidades de Shape.
     */
    private enum Shape {
        PLUS, CHART, REPORT, CATEGORY, CALCULATOR, REFRESH, PDF, CLOSE
    }

    /**
     * Tipo responsavel por funcionalidades de LineIcon.
     */
    private static final class LineIcon implements Icon {
        /**
         * Tamanho padrao do icone.
         */
        private static final int SIZE = 16;

        /**
         * Cor usada para pintar o icone.
         */
        private final Color color;
        /**
         * Forma grafica usada pelo icone.
         */
        private final Shape shape;

        /**
         * Cria um icone baseado em linha.
         *
         * @param color cor usada para desenhar o icone
         * @param shape forma grafica do icone
         */
        private LineIcon(Color color, Shape shape) {
            this.color = color;
            this.shape = shape;
        }

        /**
         * Retorna a largura do icone.
         *
         * @return valor inteiro calculado
         */
        @Override
        public int getIconWidth() {
            return SIZE;
        }

        /**
         * Retorna a altura do icone.
         *
         * @return valor inteiro calculado
         */
        @Override
        public int getIconHeight() {
            return SIZE;
        }

        /**
         * Desenha o icone no componente informado.
         *
         * @param c componente onde o icone sera desenhado
         * @param g contexto grafico usado no desenho
         * @param x coordenada horizontal
         * @param y coordenada vertical
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.translate(x, y);

            switch (shape) {
                case PLUS -> drawPlus(g2);
                case CHART -> drawChart(g2);
                case REPORT -> drawReport(g2);
                case CATEGORY -> drawCategory(g2);
                case CALCULATOR -> drawCalculator(g2);
                case REFRESH -> drawRefresh(g2);
                case PDF -> drawPdf(g2);
                case CLOSE -> drawClose(g2);
            }

            g2.dispose();
        }

        /**
         * Executa a rotina drawPlus.
         *
         * @param g2 parametro g2
         */
        private void drawPlus(Graphics2D g2) {
            g2.drawLine(8, 3, 8, 13);
            g2.drawLine(3, 8, 13, 8);
        }

        /**
         * Executa a rotina drawChart.
         *
         * @param g2 parametro g2
         */
        private void drawChart(Graphics2D g2) {
            g2.drawLine(3, 13, 13, 13);
            g2.drawLine(3, 13, 3, 3);
            g2.drawLine(5, 10, 7, 8);
            g2.drawLine(7, 8, 9, 9);
            g2.drawLine(9, 9, 12, 5);
            g2.fillOval(4, 9, 2, 2);
            g2.fillOval(6, 7, 2, 2);
            g2.fillOval(8, 8, 2, 2);
            g2.fillOval(11, 4, 2, 2);
        }

        /**
         * Executa a rotina drawReport.
         *
         * @param g2 parametro g2
         */
        private void drawReport(Graphics2D g2) {
            g2.drawRoundRect(4, 2, 8, 12, 2, 2);
            g2.drawLine(6, 6, 10, 6);
            g2.drawLine(6, 9, 10, 9);
            g2.drawLine(6, 12, 9, 12);
        }

        /**
         * Executa a rotina drawCategory.
         *
         * @param g2 parametro g2
         */
        private void drawCategory(Graphics2D g2) {
            g2.drawRoundRect(3, 4, 4, 4, 2, 2);
            g2.drawRoundRect(9, 4, 4, 4, 2, 2);
            g2.drawRoundRect(3, 10, 4, 4, 2, 2);
            g2.drawRoundRect(9, 10, 4, 4, 2, 2);
        }

        /**
         * Executa a rotina drawCalculator.
         *
         * @param g2 parametro g2
         */
        private void drawCalculator(Graphics2D g2) {
            g2.drawRoundRect(4, 2, 8, 12, 2, 2);
            g2.drawLine(6, 5, 10, 5);
            g2.fillOval(6, 8, 1, 1);
            g2.fillOval(9, 8, 1, 1);
            g2.fillOval(6, 11, 1, 1);
            g2.fillOval(9, 11, 1, 1);
        }

        /**
         * Executa a rotina drawRefresh.
         *
         * @param g2 parametro g2
         */
        private void drawRefresh(Graphics2D g2) {
            g2.drawArc(3, 3, 10, 10, 35, 250);
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(11, 3);
            arrow.lineTo(13, 3);
            arrow.lineTo(13, 5);
            g2.draw(arrow);
        }

        /**
         * Executa a rotina drawPdf.
         *
         * @param g2 parametro g2
         */
        private void drawPdf(Graphics2D g2) {
            g2.drawRoundRect(4, 2, 8, 12, 2, 2);
            g2.drawLine(6, 6, 10, 6);
            g2.drawLine(6, 9, 10, 9);
            g2.drawLine(6, 12, 8, 12);
        }

        /**
         * Executa a rotina drawClose.
         *
         * @param g2 parametro g2
         */
        private void drawClose(Graphics2D g2) {
            g2.drawLine(4, 4, 12, 12);
            g2.drawLine(12, 4, 4, 12);
        }
    }
}
