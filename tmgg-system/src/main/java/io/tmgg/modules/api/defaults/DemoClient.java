package io.tmgg.modules.api.defaults;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import io.tmgg.jackson.JsonTool;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DemoClient {

    public static void main(String[] args) {
        HttpRequest http = HttpUtil.createPost("http://127.0.0.1:8002/api/gateway");
        http.header("X-APP-KEY", "1");
        http.header("X-TIMESTAMP", String.valueOf(System.currentTimeMillis()));
        http.header("X-URI", "time");

        Map<String, Object> params = new TreeMap<>();
        params.put("format", "yyyy-MM-dd");
        params.put("x", "yyyy-MM-dd");

        String json = JsonTool.toJsonQuietly(params);
        http.body(json);

        String body = http.execute().body();
        System.out.println(body);
    }
}
