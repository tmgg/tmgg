package io.tmgg.modules.openapi.defaults;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.modules.openapi.OpenApi;
import io.tmgg.modules.openapi.gateway.BaseOpenApi;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ServerTimeOpenApi implements BaseOpenApi {


	@OpenApi(name = "服务器时间", action = "server.time")
	public Map<String, Object> time(String format) {
		Date now = new Date();
		Map<String, Object> data = new HashMap<>();
        data.put("serverTime",System.currentTimeMillis());

		if(StrUtil.isNotEmpty(format)){
			data.put("serverTimeFormat", DateUtil.format(now, format));
		}

		return data;
	}


}
