package com.sistema.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilitário para formatação de valores monetários em Real Brasileiro.
 */
public class CurrencyUtil {

    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(LOCALE_BR);
    private static final Pattern FORMATO_MOEDA = Pattern.compile(
            "^(R\\$)?\\s*(\\d+([,.]\\d{1,2})?|\\d{1,3}(\\.\\d{3})+(,\\d{1,2})?)$");

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
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Valor monetario invalido.");
        }
        String limpo = texto
                .replace("R$", "")
                .replace(" ", "")
                .replace(".", "")
                .replace(",", ".")
                .trim();
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor monetario invalido.");
        }
    }
}
        if (texto == null || texto.isBlank()) {
            throw criarErroValorInvalido(null);
        }

        String normalizado = texto.trim();
        if (!FORMATO_MOEDA.matcher(normalizado).matches()) {
            throw criarErroValorInvalido(null);
        }

        String limpo = normalizarValor(normalizado);
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            throw criarErroValorInvalido(e);
        }
    }

    private static String normalizarValor(String texto) {
        String limpo = texto
                .replace("R$", "")
                .replace(" ", "")
                .trim();

        if (limpo.contains(",")) {
            return limpo.replace(".", "").replace(",", ".");
        }
        if (limpo.matches("\\d{1,3}(\\.\\d{3})+")) {
            return limpo.replace(".", "");
        }
        return limpo;
    }

    private static IllegalArgumentException criarErroValorInvalido(Throwable causa) {
        return new IllegalArgumentException("Valor monetario invalido. Use um formato como 123,45.", causa);
    }
}

 main
