package com.sistema.view;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Filtro de documento responsavel por formatar campos monetarios.
 */
class MoneyDocumentFilter extends DocumentFilter {

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
     * Valor monetario armazenado em centavos.
     */
    private long valorCentavos;
    /**
     * Indica se o campo esta sendo atualizado internamente.
     */
    private boolean atualizandoValor;

    /**
     * Aplica o filtro monetario ao campo informado.
     *
     * @param campo campo de texto que recebera a formatacao
     */
    static void aplicar(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new MoneyDocumentFilter());
        campo.setHorizontalAlignment(SwingConstants.LEFT);
        campo.setText("0,00");
    }

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
            limparTexto(fb);
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

    /**
     * Limpa os campos e resultados exibidos na tela.
     *
     * @param fb objeto de acesso ao documento filtrado
     * @throws BadLocationException se ocorrer erro ao alterar o documento
     */
    private void limparTexto(FilterBypass fb) throws BadLocationException {
        atualizandoValor = true;
        fb.replace(0, fb.getDocument().getLength(), "", null);
        atualizandoValor = false;
    }
}
