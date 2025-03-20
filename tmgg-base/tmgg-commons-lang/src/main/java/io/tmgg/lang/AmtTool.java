package io.tmgg.lang;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额处理类
 */
public class AmtTool {

    /**
     * 元转分
     *
     * @return
     */
    public static int yuanToFen(BigDecimal yuan) {
        // 乘以100并四舍五入到最接近的整数
        BigDecimal fenBigDecimal = yuan.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.DOWN);
        // 转换为int
        return fenBigDecimal.intValueExact();
    }

}
