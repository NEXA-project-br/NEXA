package com.sistema.view;

import javax.swing.ImageIcon;
import java.awt.Window;
import java.util.Objects;

/**
 * Utilitario para aplicar a logo do NEXA nas janelas do aplicativo.
 */
public final class AppIconUtil {

    /**
     * Caminho da logo empacotada nos resources.
     */
    private static final String LOGO_PATH = "/images/nexa2.png";

    private AppIconUtil() {
    }

    /**
     * Define a logo do aplicativo como icone da janela informada.
     *
     * @param window janela que recebera o icone
     */
    public static void aplicar(Window window) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                AppIconUtil.class.getResource(LOGO_PATH),
                "Logo nao encontrada em " + LOGO_PATH));
        window.setIconImage(icon.getImage());
    }
}
