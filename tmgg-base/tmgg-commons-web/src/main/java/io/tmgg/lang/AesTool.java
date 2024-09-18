package io.tmgg.lang;

import cn.hutool.crypto.SecureUtil;

/**
 * AES 加密解密工具
 */
public class AesTool {
    private static final String key = "61GmaSlKCd4@mxvc";

    /**
     * 加密
     * @param text
     * @return
     */
    public static String encryptHex(String text) {
        return SecureUtil.aes(key.getBytes()).encryptHex(text);
    }

    /**
     * 解密
     * @param token
     * @return
     */
    public static String decryptHex(String token) {
        return SecureUtil.aes(key.getBytes()).decryptStr(token);
    }
}
