
# 开放接口

## 1 示例
可参考内置的获取时间接口
```java
package io.tmgg.modules.openapi.defaults;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.modules.openapi.OpenApi;
import io.tmgg.modules.openapi.OpenApiField;
import io.tmgg.modules.openapi.gateway.BaseOpenApi;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ServerTimeOpenApi implements BaseOpenApi {


	@OpenApi(name = "服务器时间", action = "server.time")
	public ServerTimeResponse time(@OpenApiField(required = false,desc = "时间格式") String format) {
		Date now = new Date();


		ServerTimeResponse res = new ServerTimeResponse();
		res.serverTime = System.currentTimeMillis();

		if(StrUtil.isNotEmpty(format)){
			res.serverTimeFormat =  DateUtil.format(now, format);
		}

		return res;
	}


    // 响应数据封装
	@Data
	public static class ServerTimeResponse {

		@OpenApiField(required = true, desc = "服务器时间")
		long serverTime;

		@OpenApiField(required = false, desc = "服务器时间格式化")
		String serverTimeFormat;
	}

}

```



## 2 第三方如何调用
## 2.1 创建账号
在管理界面创建账号，获得appid、密钥
## 2.2 查看、下载文档
可查看何打印文档，如需下载文档，则打印时选择pdf即可

## 2.3 sdk
如果对方是java开发,可以使用sdk
```xml
        <dependency>
            <groupId>io.github.tmgg</groupId>
            <artifactId>tmgg-openapi-sdk</artifactId>
        </dependency>
```

代码示例
```java
        OpenApiSdk sdk = new OpenApiSdk("https://xxx.com", appId, appSecrect);

        Map<String, Object> params = new HashMap<>();
        params.put("参数1","值1");
        params.put("参数2","值2");
        
        String send = sdk.send("send", params);
        System.out.println("响应：" + send);
```
