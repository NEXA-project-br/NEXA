package com.sistema.view;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

class MoneyDocumentFilter extends DocumentFilter {

    private static final NumberFormat FMT_VALOR = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
    private static final int MAX_DIGITOS_CENTAVOS = 15;

    static {
        FMT_VALOR.setMinimumFractionDigits(2);
        FMT_VALOR.setMaximumFractionDigits(2);
    }

    private long valorCentavos;
    private boolean atualizandoValor;

    static void aplicar(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new MoneyDocumentFilter());
        campo.setHorizontalAlignment(SwingConstants.LEFT);
        campo.setText("0,00");
    }

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
        boolean substituiTudo = offset == 0 && length == fb.getDocument().getLength();
        if (text == null || text.isEmpty()) {
            valorCentavos = 0L;
            if (substituiTudo) {
                limparTexto(fb);
                return;
            }
        } else {
            if (substituiTudo) {
                valorCentavos = 0L;
            }
            processarEntrada(fb, text);
            return;
        }
        substituirTexto(fb);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        if (atualizandoValor) {
            fb.remove(offset, length);
            return;
        }
        if (offset == 0 && length == fb.getDocument().getLength()) {
            valorCentavos = 0L;
            limparTexto(fb);
            return;
        }
        valorCentavos /= 10;
        substituirTexto(fb);
    }

    private void processarEntrada(FilterBypass fb, String texto) throws BadLocationException {
        if (texto == null) {
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

    private void substituirTexto(FilterBypass fb) throws BadLocationException {
        atualizandoValor = true;
        fb.replace(0, fb.getDocument().getLength(),
                FMT_VALOR.format(BigDecimal.valueOf(valorCentavos, 2)), null);
        atualizandoValor = false;
    }

    private void limparTexto(FilterBypass fb) throws BadLocationException {
        atualizandoValor = true;
        fb.replace(0, fb.getDocument().getLength(), "", null);
        atualizandoValor = false;
    }
}
