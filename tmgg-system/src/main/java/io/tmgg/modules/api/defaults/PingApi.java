package io.tmgg.modules.api.defaults;

import cn.hutool.core.date.DateUtil;
import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.api.Api;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class PingApi {

	@Api(name = "测试连通性", uri = "ping", desc = "示例接口，为了测试，会返回pong")
	public String ping(@FieldInfo(label = "信息", len = 50) String msg) {
		return "pong:" + msg;
	}

	@Api(name = "获得服务器时间", uri = "time")
	public TimeInfo time() {
		return new TimeInfo(DateUtil.now(), System.currentTimeMillis());
	}

	@Data
	@AllArgsConstructor
	public static class TimeInfo {
		@FieldInfo(label = "格式化时间")
		String time;

		@FieldInfo(label = "时间戳")
		long timestamp;
	}
}
