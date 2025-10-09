package io.tmgg.api;

import io.tmgg.jackson.JsonTool;
import io.tmgg.modules.api.ApiClient;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApiTest {

    @Resource
    ApiAccountResourceService accountResourceService;

    @Resource
    ApiAccountService accountService;

    @Resource
    ApiResourceService apiResourceService;



    @LocalServerPort
    private int port;


    @BeforeEach
    public void init() {
        ApiAccount a = new ApiAccount();
        a.setName("测试");
        a.setAppId("appId");
        a.setAppSecret("appSecret");
        a.setEnable(true);
        a = accountService.save(a);

        ApiResource r = apiResourceService.findAction("math.add");


        ApiAccountResource ar = new ApiAccountResource();
        ar.setAccount(a);
        ar.setEnable(true);
        ar.setResource(r);
        accountResourceService.save(ar);

        System.out.println("1");

        System.out.println("端口号" + port);
    }



    @Test
    public void test() throws IOException {
        String url = "http://127.0.0.1:" + port;
        System.out.println("请求地址:" + url);
        ApiClient client = new ApiClient(url, "appId", "appSecret");
        String result = client.send("math.add", Map.of("a", 1, "b", 2));
        System.out.println(result);

        Map<String, Object> map = JsonTool.jsonToMap(result);
        Object data = map.get("data");

        Assertions.assertEquals(3,data);
    }

}
