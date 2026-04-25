package com.sistema.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DateDocumentFilter extends DocumentFilter {

    private static final int MAX_DIGITOS = 8;

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String candidato = atual.substring(0, offset) + (text == null ? "" : text) + atual.substring(offset + length);
        String apenasDigitos = candidato.replaceAll("\\D", "");

        if (apenasDigitos.length() > MAX_DIGITOS) {
            apenasDigitos = apenasDigitos.substring(0, MAX_DIGITOS);
        }

        String formatado = formatar(apenasDigitos);
        fb.replace(0, fb.getDocument().getLength(), formatado, attrs);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        replace(fb, offset, length, "", null);
    }

    private String formatar(String digitos) {
        StringBuilder data = new StringBuilder();

        for (int i = 0; i < digitos.length(); i++) {
            if (i == 2 || i == 4) {
                data.append('/');
            }
            data.append(digitos.charAt(i));
        }

        return data.toString();
    }
}
