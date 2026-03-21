package com.sistema.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitário para formatação de valores monetários em Real Brasileiro.
 */
public class CurrencyUtil {

    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(LOCALE_BR);

    private CurrencyUtil() {}

    /**
     * Formata um BigDecimal para String no padrão "R$ 1.234,56".
     */
    public static String formatar(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return FORMATTER.format(valor);
    }

    /**
     * Converte uma String de moeda para BigDecimal.
     * Aceita formatos como "1234,56", "R$ 1.234,56", "1234.56".
     */
    public static BigDecimal parsear(String texto) {
        if (texto == null || texto.isBlank()) return BigDecimal.ZERO;
        String limpo = texto
                .replace("R$", "")
                .replace(" ", "")
                .replace(".", "")
                .replace(",", ".")
                .trim();
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}