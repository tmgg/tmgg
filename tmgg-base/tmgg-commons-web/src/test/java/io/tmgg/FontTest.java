package io.tmgg;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Locale;

public class FontTest {
    public static void main(String[] args) {
        Font[] allFonts = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAllFonts();

        System.out.println("系统中的字体:");
        for (Font font : allFonts) {

                System.out.println(font.getFontName() + " (" + font.getFamily() + ")");

        }
    }


}
