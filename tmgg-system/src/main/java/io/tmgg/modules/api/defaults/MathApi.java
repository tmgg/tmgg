package io.tmgg.modules.api.defaults;

import io.tmgg.modules.api.ApiMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class MathApi {



	@ApiMapping(action = "math.add", name = "加法计算", desc = "根据输入参数a，b，计算a+b的结果")
	public int add(int a, int b){
		return a +b;
	}


	@ApiMapping(action = "math.add2", name = "加法计算2", desc = "根据输入参数a，b，计算a+b的结果")
	public AddOutput add2(AddInput addInput){
		int a = addInput.getA();
		int b = addInput.getB();
		int sum = a + b;

		return new AddOutput(sum);
	}

	@Data
	public static class AddInput{
		private int a;
		private int b;
	}

	@AllArgsConstructor
	@Data
	public static class AddOutput{
		private int sum;
	}

}
