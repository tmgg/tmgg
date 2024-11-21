package io.tmgg.web.jwt;


import io.tmgg.jackson.JsonTool;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import java.util.Base64;

/**
 * jwt标准的简单实现
 * <p>
 * 原理：http://blog.leapoahead.com/2015/09/06/understanding-jwt/
 * <p>
 * 深度实现请参考 1 官网
 */
public class Jwt {


    // key: 32 bytes
    private static final byte[] SECRET = "56fee667491742eb8e29e46cb5a3c185".getBytes();

    private static final String HEADER = "{\"alg\":\"HS256\",\"type\":\"JWT\"}";
    private static final String HEADER_BASE64 = Base64.getUrlEncoder().encodeToString(HEADER.getBytes());



    /**
     * 生成token，该方法只在用户登录成功后调用
     * <p>
     * header.payload.sign
     * <p>
     * { "alg":"HS256", "type":"JWT" }
     */
    public static String createToken(JwtPayload payload) {
        String payloadJson =  JsonTool.toJsonQuietly(payload);
        String payloadBase64 = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes());

        String content = HEADER_BASE64 + "." + payloadBase64;
        String sign = sign(content);

        String token = content + "." + sign;
        return token;

    }


    /**
     * @param token
     * @return null if invalid
     */
    public static JwtPayload validate(String token) throws JwtExpireException, JwtInvalidException, JwtNotFountException {
        if (token == null) {
            throw new JwtNotFountException();
        }
        token = token.replaceAll(" ", "+");
        String[] info = token.split("\\.");
        if (info.length != 3) {
            throw new JwtInvalidException("登录凭证应该由3部分组成" + token);
        }

        String headerBase64 = info[0];
        String payloadBase64 = info[1];
        String requestSign = info[2];

        String signData = headerBase64 + "." + payloadBase64;
        String validSign = sign(signData);
        boolean signValid = validSign.equals(requestSign);
        if (!signValid) {
            throw new JwtInvalidException("登录凭证的签名错误");
        }

        String payloadStr = new String(Base64.getUrlDecoder().decode(payloadBase64));
        JwtPayload jwtPayload = JsonTool.jsonToBeanQuietly(payloadStr, JwtPayload.class);
        long now = System.currentTimeMillis();
        boolean timeValid = jwtPayload.getExp() > now;
        if (!timeValid) {
            throw new JwtExpireException(jwtPayload);
        }
        return jwtPayload;
    }


    /**
     * 该方法带缓存，_sign为真正的实现类
     *
     * @param content
     * @return
     */
    private static String sign(String content) {
        return _sign(content);
    }

    // 忽略header指定的算法， 这里只有一种签名算法
    private static String _sign(String content) {
        byte[] data = content.getBytes();

        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, SECRET);
        byte[] signData = mac.doFinal(data);

        return Base64.getUrlEncoder().encodeToString(signData);
    }


}
