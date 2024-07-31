package io.tmgg.lang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Tool {

    private static final int BYTE_BLOCK = 256;
    private static final int MAX_HEX = 16;

    public static String md5(String str) {
        byte[] bytes = str.getBytes();
        return md5(bytes);
    }

    public static String md5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);


            byte[] byteDigest = md.digest();

            int i;
            StringBuilder buf = new StringBuilder();
            for (byte element : byteDigest) {
                i = element;
                if (i < 0) {
                    i += BYTE_BLOCK;
                }

                if (i < MAX_HEX) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}
