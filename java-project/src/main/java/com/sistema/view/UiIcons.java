package com.sistema.view;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

final class UiIcons {

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

    private static Icon icon(Color color, Shape shape) {
        return new LineIcon(color, shape);
    }

    private enum Shape {
        PLUS, CHART, REPORT, CATEGORY, CALCULATOR, REFRESH, PDF, CLOSE
    }

    private static final class LineIcon implements Icon {
        private static final int SIZE = 16;

        private final Color color;
        private final Shape shape;

        private LineIcon(Color color, Shape shape) {
            this.color = color;
            this.shape = shape;
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }

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

        private void drawPlus(Graphics2D g2) {
            g2.drawLine(8, 3, 8, 13);
            g2.drawLine(3, 8, 13, 8);
        }

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

        private void drawReport(Graphics2D g2) {
            g2.drawRoundRect(4, 2, 8, 12, 2, 2);
            g2.drawLine(6, 6, 10, 6);
            g2.drawLine(6, 9, 10, 9);
            g2.drawLine(6, 12, 9, 12);
        }

        private void drawCategory(Graphics2D g2) {
            g2.drawRoundRect(3, 4, 4, 4, 2, 2);
            g2.drawRoundRect(9, 4, 4, 4, 2, 2);
            g2.drawRoundRect(3, 10, 4, 4, 2, 2);
            g2.drawRoundRect(9, 10, 4, 4, 2, 2);
        }

        private void drawCalculator(Graphics2D g2) {
            g2.drawRoundRect(4, 2, 8, 12, 2, 2);
            g2.drawLine(6, 5, 10, 5);
            g2.fillOval(6, 8, 1, 1);
            g2.fillOval(9, 8, 1, 1);
            g2.fillOval(6, 11, 1, 1);
            g2.fillOval(9, 11, 1, 1);
        }

        private void drawRefresh(Graphics2D g2) {
            g2.drawArc(3, 3, 10, 10, 35, 250);
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(11, 3);
            arrow.lineTo(13, 3);
            arrow.lineTo(13, 5);
            g2.draw(arrow);
        }

        private void drawPdf(Graphics2D g2) {
            g2.drawRoundRect(4, 2, 8, 12, 2, 2);
            g2.drawLine(6, 6, 10, 6);
            g2.drawLine(6, 9, 10, 9);
            g2.drawLine(6, 12, 8, 12);
        }

        private void drawClose(Graphics2D g2) {
            g2.drawLine(4, 4, 12, 12);
            g2.drawLine(12, 4, 4, 12);
        }
    }
}
