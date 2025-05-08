package io.github.tmgg.openapi.sdk;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 供第三方调用
 */
public class OpenApiSdk {

    private String baseUrl;
    private String appId;
    private String appSecret;


    @Setter
    private boolean debug = false;


    public OpenApiSdk(String baseUrl, String appId, String appSecret) {
        this.baseUrl = baseUrl;
        this.appId = appId;
        this.appSecret = appSecret;
    }


    public String send(String action, Map<String, Object> params) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = baseUrl + "/openApi/gateway";
        String postData = this.joinParams(params);


        Map<String, String> headers = new HashMap<>();
        headers.put("x-action", action);
        headers.put("x-app-id", appId);
        headers.put("x-timestamp", timestamp);

        // 签名
        String sign = sign(action, appId, timestamp, postData);
        headers.put("x-signature", sign);


        String body = HttpUtil.createPost(url)
                .headerMap(headers, true)
                .body(postData)
                .execute().body();
        if (debug) {
            System.out.println("响应原始数据");
            System.out.println(body);
        }

        // 成功
        return body;
    }

    private String sign(String action, String appId, String timestamp, String postData) {
        StringBuilder sb = new StringBuilder();
        sb.append(action).append("\n");
        sb.append(appId).append("\n");
        sb.append(timestamp).append("\n");
        sb.append(postData);

        String signStr = sb.toString();
        return SecureUtil.hmacSha256(appSecret).digestBase64(signStr, false);
    }


    private String joinParams(Map<String, Object> params) {
        params = params == null ? new TreeMap<>() : new TreeMap<>(params);

        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (v != null) {
                sb.append(k).append("=").append(v).append("&");
            }
        });
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        String url = "http://127.0.0.1:8002";
        String appId = "1589fcbd4a0b4a5cb01094f75dc52ac3";
        String appSecret = "D4cy5ofnQ46RCCd8FGe1YYbunZadOXAf";

        OpenApiSdk sdk = new OpenApiSdk(url, appId, appSecret);

        String action = "server.time";

        Map<String, Object> params = new TreeMap<>();
        params.put("format", "yyyy-MM-dd HH:mm:ss");
        params.put("bbb", "中 国 人");
        params.put("aaa", "中国人");

        String result = sdk.send(action, params);

        System.out.println("响应的json数据为：" + result);
    }

}
