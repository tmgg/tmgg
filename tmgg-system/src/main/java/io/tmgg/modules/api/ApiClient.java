package io.tmgg.modules.api;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 调用sdk
 */
@Slf4j
public class ApiClient {

    private String baseUrl;
    private String appId;
    private String appSecret;


    public ApiClient(String baseUrl, String appId, String appSecret) {
        this.baseUrl = baseUrl;
        this.appId = appId;
        this.appSecret = appSecret;
    }


    public String send(String action, Map<String, Object> params) {
        long timestamp = System.currentTimeMillis();
        String url = baseUrl + "/openApi/gateway/" + action;


        String sign = ApiSignTool.sign(appId, appSecret, timestamp);


        HttpResponse response = HttpUtil.createPost(url)
                .header("appId",appId)
                .header("timestamp", String.valueOf(timestamp))
                .header("sign", sign)
                .form(params)
                .execute();

        log.info("返回：\n{}",response);

        // 成功
        return response.body();
    }


    public static void main(String[] args) throws IOException {
        String url = "http://127.0.0.1:8002";
        String appId = "220e0de1701041a0b8ae029704f09f5f";
        String appSecret = "nVgyPYn10M6ZYgbdHRKmLIUcbc5LFUly";

        ApiClient client = new ApiClient(url, appId, appSecret);

        Map<String,Object> params = new HashMap<>();
        params.put("a",1);
        params.put("b",2);
        client.send("add", params);
    }

}
