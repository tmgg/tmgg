package io.tmgg.lang;

import cn.hutool.core.text.PasswdStrength;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.Assert;

import static cn.hutool.core.util.RandomUtil.BASE_CHAR_NUMBER;

public class PasswordTool {

    public static String random(){
        return RandomUtil.randomString(BASE_CHAR_NUMBER +"_-!.@$^&*()+=",12);
    }

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
