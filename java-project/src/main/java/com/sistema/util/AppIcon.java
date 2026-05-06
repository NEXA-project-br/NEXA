package com.sistema.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza o icone usado nas janelas do aplicativo.
 */
public final class AppIcon {

    private static final String ICON_PATH = "/images/nexa.png";
    private static final int[] ICON_SIZES = {16, 24, 32, 48, 64, 128, 256};
    private static List<Image> cachedIcons;

    private AppIcon() {}

    public static void aplicar(Window janela) {
        janela.setIconImages(obterIcones());
    }

    private static List<Image> obterIcones() {
        if (cachedIcons == null) {
            cachedIcons = carregarIcones();
        }
        return cachedIcons;
    }

    private static List<Image> carregarIcones() {
        try {
            BufferedImage original = ImageIO.read(AppIcon.class.getResource(ICON_PATH));
            List<Image> icones = new ArrayList<>();
            for (int tamanho : ICON_SIZES) {
                icones.add(original.getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH));
            }
            return icones;
        } catch (IOException e) {
            throw new UncheckedIOException("Nao foi possivel carregar o icone do aplicativo.", e);
        }
    }
}
