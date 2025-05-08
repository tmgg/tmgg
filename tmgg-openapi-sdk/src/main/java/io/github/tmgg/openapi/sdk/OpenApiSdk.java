package io.github.tmgg.openapi.sdk;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 第三方调用sdk
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
        headers.put("x-request-id", IdUtil.fastUUID());

        // 签名
        String sign = sign(action, appId, timestamp, postData);
        headers.put("x-signature", sign);


        HttpResponse response = HttpUtil.createPost(url)
                .headerMap(headers, true)
                .body(postData)
                .execute();
        String body = response.body();
        if (debug) {
            System.out.println("http状态码" + response.getStatus());
            System.out.println("响应原始数据");
            System.out.println(body);
        }

        // 成功
        return body;
    }

    private String sign(String action, String appId, String timestamp, String body) {
        String signStr = action + "\n" + appId + "\n" + timestamp + "\n" + body;
        return SecureUtil.hmacSha256(appSecret).digestHex(signStr);
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
        String appId = "2d9033f0e76f47ef9af4e25e3d3e161c";
        String appSecret = "y8rRaheXGw3BUQMzf4fimFz61VdZ28Dy";

        OpenApiSdk sdk = new OpenApiSdk(url, appId, appSecret);
        sdk.setDebug(true);


        String result = sdk.send("user.name", MapUtil.of("account","superAdmin"));
    }

}
