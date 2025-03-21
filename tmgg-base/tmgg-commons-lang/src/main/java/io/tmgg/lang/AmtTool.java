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
        BigDecimal dFen = yuan.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.HALF_UP);
        return dFen.intValueExact();
    }
    public static BigDecimal fenToYuan(int fen) {
        BigDecimal dFen = new BigDecimal(fen);

        BigDecimal y = dFen.divide(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
        return y;
    }


}
