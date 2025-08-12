package io.tmgg.lang;

import cn.hutool.core.util.NumberUtil;

public class NumberTool {

    public static Number tryGetNumber(String str) {
        if (!NumberUtil.isNumber(str)) {
            return null;
        }

        if (str.contains(".")) {
            return Float.parseFloat(str);
        }

        return Integer.parseInt(str);
    }


}
