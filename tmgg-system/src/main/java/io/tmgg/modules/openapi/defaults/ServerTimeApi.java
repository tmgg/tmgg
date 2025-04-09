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
public class ServerTimeApi implements BaseOpenApi {


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


	@Data
	public static class ServerTimeResponse {

		@OpenApiField(required = true, desc = "服务器时间")
		long serverTime;

		@OpenApiField(required = false, desc = "服务器时间格式化")
		String serverTimeFormat;
	}

}
