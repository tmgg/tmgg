package io.tmgg.lang;

import cn.hutool.core.text.PasswdStrength;
import org.springframework.util.Assert;

public class PasswordTool {

    /**
     * 生产密码的密文，每次调用都不一样
     * @param plainText
     *
     */
    public static String encode(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    public static boolean checkpw(String password, String passwordBcrypt) {
        return BCrypt.checkpw(password, passwordBcrypt);
    }


    /**
     * 校验密码强度
     * @param password
     */
    public static void validateStrength(String password) {
        Assert.state(isStrengthOk(password), "密码强度太低");

    }

    public static boolean isStrengthOk(String password) {
        return PasswdStrength.getLevel(password).ordinal() > PasswdStrength.PASSWD_LEVEL.EASY.ordinal();
    }

}
