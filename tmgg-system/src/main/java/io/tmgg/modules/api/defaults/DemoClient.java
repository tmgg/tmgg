package io.tmgg.modules.api.defaults;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import io.tmgg.jackson.JsonTool;

import java.util.Map;
import java.util.TreeMap;

public class DemoClient {


    public static void main(String[] args) {
        String appSecret = "61GmaSlKCd4@tmgg";
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKiHuMTi+8jNfyBj8gidcAecPZ3hKUbvLnp/rUDoneOMpIuWMFHtqZA7+vV8x1+qh/DXVTWeyerTVSYIMFD82pSJtyOun7Xnba3EDNtnvRR6kw7oUVNiYORmSXk8aB5IwU6E166LjCYccfbG5MI+gIISpSCG7vKIJvl2pHyT1l0lAgMBAAECgYADpTE5RI65p6EiwY3JHCQBwpQP8mP0nmLrM9dXTE04pa7N5wWE/JbOHd4DjVSAAB+eZxu+R501Im5qnp+c64JTQA32GRu1OvmrOldAP8Wh/7Z6J1NVCtlyNIo1Q3i4ZAqKZ2v9mu33kd9AiED/DHaoCJO81bHjfJTwyNcU0bfVyQJBAOBUfdnbtXlKSb3P9TtLPL1gQCmNpA0v2NS0v2v2xPxCTv/oAl5tRXsfTLGbEuAEwH8AOoFJ7SqEAWIc6jEUEe8CQQDAUpFKjAl/O4Nn1PjPhWNa3sF/SeYo9WEsDa2Xf7NgmCkaPC3BfANfhOBZoxBYMiUpViVfP5hxlczHDz0AcEYrAkA7PbTago4DEN42dSOi2/SXYkKvdos7PEZ7rZvbjBhrMauhIwj1HNA7eoGLaZNre3XGVC4fFIHAN4oR+ebSvSUbAkA5TOBo+4wg0lR2bSesfKt7eX7hM91VOra272RXDP8euncUj/V5/j7rCKXnTwQhot8bj8UWudXrkZwvesTMCqd9AkAQojuV3N6OhfuiPKEjRMyMIin9xUCzwi/NFQDjMfPvbH7fE9ftvdaB0WrKhCTEr38/Nmfwk8GHfPsTRH5aEs78";
        String appKey = "1";


        String timestamp = String.valueOf(System.currentTimeMillis());
        String uri = "time";
        boolean encrypt = false; // 是否需要加密

        HttpRequest http = HttpUtil.createPost("http://127.0.0.1:8002/api/gateway");
        http.header("X-APP-KEY", appKey);
        http.header("X-TIMESTAMP", timestamp);
        http.header("X-URI", uri);

        Map<String, Object> params = new TreeMap<>();
        params.put("format", "yyyy-MM-dd");
        String data = JsonTool.toJsonQuietly(params);

        // 签名
        {
            String signSource = uri + appKey + timestamp + data;
            String signResult = SecureUtil.md5(signSource);
            String sign = SecureUtil.rsa(privateKey, null).encryptBase64(signResult, KeyType.PrivateKey);
            http.header("X-SIGNATURE", sign);
            System.out.println("签名为：" + sign);
        }


        AES aes = SecureUtil.aes(appSecret.getBytes());
        if (encrypt) {
            data = aes.encryptHex(data);  // 加密，如果需要的情况下
        }

        http.form("data", data);


        String body = http.execute().body();

        System.out.println("响应内容");
        System.out.println(body);

        if (encrypt) {
            String decryptStr = aes.decryptStr(body);
            System.out.println("解密内容");
            System.out.println(decryptStr);
        }


    }
}
