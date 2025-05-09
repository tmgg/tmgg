package io.tmgg;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

public class FontExistTest {
    public static void main(String[] args) {

        boolean x = isFontInstalled("xba");
        System.out.println(x);
    }

    public static boolean isFontInstalled(String fontName) {
        try {
            // 尝试创建字体对象
            Font font = new Font(fontName, Font.PLAIN, 12);
            // 检查创建后的字体名称是否与请求的一致
            return font.getFamily().equalsIgnoreCase(fontName)
                    || font.getName().equalsIgnoreCase(fontName);
        } catch (Exception e) {
            return false;
        }
    }
}
