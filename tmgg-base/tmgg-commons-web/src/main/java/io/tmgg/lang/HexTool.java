package io.tmgg.lang;

public class HexTool {

    public static String encode(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0'); // 如果是单个字符的十六进制，前面补零
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] decode(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                       + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }


}
