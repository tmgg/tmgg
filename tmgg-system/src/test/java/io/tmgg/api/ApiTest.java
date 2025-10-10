package io.tmgg.api;

import io.tmgg.jackson.JsonTool;
import io.tmgg.modules.api.ApiClient;
import io.tmgg.modules.api.ApiErrorCode;
import io.tmgg.modules.api.entity.ApiAccount;
import io.tmgg.modules.api.entity.ApiAccountResource;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import io.tmgg.modules.api.service.ApiAccountService;
import io.tmgg.modules.api.service.ApiResourceService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApiTest {

    @Resource
    private ApiAccountResourceService accountResourceService;

    @Resource
    private ApiAccountService accountService;

    @Resource
    private ApiResourceService apiResourceService;


    @LocalServerPort
    private int port;
    private ApiClient client;


    @BeforeEach
    public void init() {
        ApiAccount a = new ApiAccount();
        a.setName("测试");
        a.setAppId("appId");
        a.setAppSecret("appSecret");
        a.setEnable(true);
        a = accountService.save(a);

        {
            ApiResource r = apiResourceService.findAction("math.add");


            ApiAccountResource ar = new ApiAccountResource();
            ar.setAccount(a);
            ar.setEnable(true);
            ar.setResource(r);
            accountResourceService.save(ar);
        }

        {
            ApiResource r = apiResourceService.findAction("math.add2");


            ApiAccountResource ar = new ApiAccountResource();
            ar.setAccount(a);
            ar.setEnable(true);
            ar.setResource(r);
            accountResourceService.save(ar);
        }


        System.out.println("1");

        System.out.println("端口号" + port);

        String url = "http://127.0.0.1:" + port;
        System.out.println("请求地址:" + url);
        this.client = new ApiClient(url, "appId", "appSecret");
    }


    @Test
    public void add() throws IOException {
        String result = client.send("math.add", Map.of("a", 1, "b", 2));
        System.out.println(result);

        Map<String, Object> map = JsonTool.jsonToMap(result);
        Object data = map.get("data");

        Assertions.assertEquals(3, data);
    }

    @Test
    public void add2() throws IOException {
        String result = client.send("math.add2", Map.of("a", 1, "b", 2));
        System.out.println(result);

        Map<String, Object> map = JsonTool.jsonToMap(result);
        Map<String, Object> data = (Map<String, Object>) map.get("data");

        Assertions.assertEquals(3, data.get("sum"));
    }


    @Test
    public void test404() throws IOException {
        String result = client.send("math.xyz", Map.of("a", 1, "b", 2));
        System.out.println(result);

        Map<String, Object> map = JsonTool.jsonToMap(result);
        Object data = map.get("code");

        Assertions.assertEquals(ApiErrorCode.RES_NOT_FOUND.getCode(), data);
    }

}
