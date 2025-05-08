package io.tmgg.modules.openapi.defaults;

import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.openapi.OpenApi;
import org.springframework.stereotype.Component;

@Component
public class PingApi {

	@OpenApi(name = "测试连通性", action = "ping", desc = "示例接口，为了测试，会返回pong")
	public String ping(@FieldInfo(label = "信息", len = 50) String msg) {
		return "pong:" + msg;
	}

}
