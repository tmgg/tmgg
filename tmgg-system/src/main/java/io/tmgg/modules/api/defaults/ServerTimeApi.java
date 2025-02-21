package io.tmgg.modules.api.defaults;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.modules.api.Api;
import io.tmgg.modules.api.gateway.BaseApi;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class ServerTimeApi implements BaseApi {


	@Api(name = "服务器时间", url = "server.time",encrypt = false)
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
