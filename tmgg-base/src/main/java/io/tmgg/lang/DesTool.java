package io.tmgg.lang;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DesTool {


    private static final String DEFAULT_KEY = "R3@l&yP8ssw0rd!";

    public static String encryptBase64(String plaintext) {
        return encryptBase64(plaintext, DEFAULT_KEY);
    }

    public static String decryptBase64(String ciphertext) {
        return decryptBase64(ciphertext, DEFAULT_KEY);
    }

    public static String encryptHex(String plaintext) {
        return encryptHex(plaintext, DEFAULT_KEY);
    }

    public static String decryptHex(String ciphertext) {
        return decryptHex(ciphertext, DEFAULT_KEY);
    }


    public static String encryptHex(String plaintext, String key) {
        byte[] encrypt = encrypt(plaintext.getBytes(StandardCharsets.UTF_8), key);
        return HexTool.encode(encrypt);
    }

    public static String decryptHex(String ciphertext, String key) {
        byte[] bytes = HexTool.decode(ciphertext);
        byte[] decrypt = decrypt(bytes, key);
        return new String(decrypt,StandardCharsets.UTF_8);
    }


    public static String encryptBase64(String plaintext, String key) {
        byte[] encrypt = encrypt(plaintext.getBytes(StandardCharsets.UTF_8), key);
        return Base64.getEncoder().encodeToString(encrypt);
    }

    public static String decryptBase64(String ciphertext, String key) {
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decrypt = decrypt(ciphertextBytes, key);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    public static byte[] encrypt(byte[] bytes, String key) {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] cipherBytes, String key) {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
