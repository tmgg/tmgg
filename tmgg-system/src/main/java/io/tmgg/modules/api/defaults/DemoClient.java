package io.tmgg.modules.api.defaults;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.tmgg.jackson.JsonTool;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class DemoClient {
    static boolean needEncrypt = false; // 是否需要加密
    static String appId = "3a5d98706edd40e5a898b39d725a20d8";
    static String appSecret = "6r5lmNbZmyrgBZ4IttVnZ8odMtzHq5rU";


    public static void main(String[] args) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String action = "server.time";


        HttpRequest http = HttpUtil.createPost("http://127.0.0.1:8002/api/gateway");
        http.header("x-app-id", appId);
        http.header("x-timestamp", timestamp);
        http.header("x-action", action);

        // 请求参数
        Map<String, Object> params = new TreeMap<>();
        params.put("format", "yyyy-MM-dd HH:mm:ss");
        String data = JsonTool.toJson(params);

        // 签名
        String sign = SecureUtil.hmacSha256(appSecret).digestBase64(action + appId + timestamp + data, false);
        http.header("x-signature", sign);
        System.out.println("签名为：" + sign);


        // 加密
        AES aes = SecureUtil.aes(appSecret.getBytes());
        if (needEncrypt) {
            data = aes.encryptHex(data);  // 加密，如果需要的情况下
            System.out.println("请求加密:" + data);
            System.out.println(aes.decryptStr(data));
        }

        http.form("data",data);


        HttpResponse response = http.execute();
        String body = response.body();


        Map<String, Object> result = JsonTool.jsonToMap(body);
        Integer code = (Integer) result.get("code");
        String msg = (String) result.get("msg");
        Object responseData = result.get("data");

        System.out.println(body);
        System.out.println(responseData);


        // 成功
        if (code == 1000) {
            if (needEncrypt) {
                String decryptStr = aes.decryptStr((String) responseData);
                System.out.println("解密内容" + decryptStr);
            }
        }

    }
}
