package io.tmgg.modules.api.defaults;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import io.tmgg.jackson.JsonTool;

import java.util.Map;
import java.util.TreeMap;

public class DemoClient {
    private static final String appSecret = "61GmaSlKCd4@tmgg";

    public static void main(String[] args) {
        HttpRequest http = HttpUtil.createPost("http://127.0.0.1:8002/api/gateway");
        http.header("X-APP-KEY", "1");
        http.header("X-TIMESTAMP", String.valueOf(System.currentTimeMillis()));
        http.header("X-URI", "time");

        Map<String, Object> params = new TreeMap<>();
        params.put("format", "yyyy-MM-dd");
        params.put("x", "yyyy-MM-dd");

        String json = JsonTool.toJsonQuietly(params);


        AES aes = SecureUtil.aes(appSecret.getBytes());

        String encryptHex = aes.encryptHex(json);
        System.out.println(encryptHex);
        http.form("data",encryptHex);

        String body = http.execute().body();
        System.out.println(body);

        String decryptStr = aes.decryptStr(body);
        System.out.println(decryptStr);
    }
}
