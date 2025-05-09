package io.tmgg.modules.api.defaults;

import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.api.Action;
import org.springframework.stereotype.Component;

@Component
public class PingApi {

	@Action(name = "测试连通性", uri = "ping", desc = "示例接口，为了测试，会返回pong")
	public String ping(@FieldInfo(label = "信息", len = 50) String msg) {
		return "pong:" + msg;
	}

}
