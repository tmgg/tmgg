package io.tmgg.lang;

import cn.hutool.crypto.SecureUtil;

/**
 * AES 加密解密工具
 */
public class AesTool {
    private static final String key = "61GmaSlKCd4@mxvc";
    public static String encryptHex(String text) {
        return SecureUtil.aes(key.getBytes()).encryptHex(text);
    }

    public static String decryptHex(String token) {
        return SecureUtil.aes(key.getBytes()).decryptStr(token);
    }
}
