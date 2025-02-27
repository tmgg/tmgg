package io.github.tmgg.openapi.sdk;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 供第三方调用
 */
public class OpenApiSdk {
    public static final int SUCCESS_CODE = 0;

    private String baseUrl;
    private String appId;
    private String appSecret;


    @Setter
    private boolean debug = true;


    public OpenApiSdk(String baseUrl, String appId, String appSecret) {
        this.baseUrl = baseUrl;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public static void main(String[] args) throws IOException {
        String url = "http://127.0.0.1:8002";
        String appId = "473428cdaeb44e27bb0f45c32a7fc2b5";
        String appSecret = "cbCaGVuSWWgSExaZTvcVG80IrKKVifI2";

        OpenApiSdk sdk = new OpenApiSdk(url, appId, appSecret);

        String action = "server.time";

        Map<String, Object> params = new TreeMap<>();
        params.put("format", "yyyy-MM-dd HH:mm:ss");

        String result = sdk.send(action, params);

        System.out.println("响应的json数据为："+ result);
    }


    public String send(String action, Map<String, Object> params) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = baseUrl + "/openApi/gateway";
        HttpRequest http = HttpUtil.createPost(url);
        http.header("x-app-id", appId);
        http.header("x-timestamp", timestamp);
        http.header("x-action", action);


        String data = JSONUtil.toJsonStr(params);
        // 签名
        String sign = SecureUtil.hmacSha256(appSecret).digestBase64(action + appId + timestamp + data, false);
        http.header("x-signature", sign);


        // 加密
        AES aes = SecureUtil.aes(appSecret.getBytes());
        data = aes.encryptHex(data);  // 加密，如果需要的情况下
        http.form("data", data);


        HttpResponse response = http.execute();
        String body = response.body();

        if(debug){
            System.out.println("响应原始数据");
            System.out.println(body);
        }

        JSON result = JSONUtil.parse(body);
        Integer code = (Integer) result.getByPath("code");
        String msg = (String) result.getByPath("msg");

        Assert.state(code == SUCCESS_CODE, msg);

        Object responseData = result.getByPath("data");

        // 成功
        String decryptStr = aes.decryptStr((String) responseData);
        return decryptStr;
    }
}
