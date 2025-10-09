package io.tmgg.modules.api.defaults;

import io.tmgg.modules.api.ApiMapping;
import org.springframework.stereotype.Component;

@Component
public class MathApi {



	@ApiMapping(action = "math.add", name = "加法计算", desc = "根据输入参数a，b，计算a+b的结果")
	public int add(int a, int b){
		return a +b;
	}


}
