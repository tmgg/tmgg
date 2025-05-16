package io.tmgg.lang;

import cn.hutool.core.util.CharUtil;
import io.tmgg.lang.obj.Between;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

public class StrTool {


    /**
     * 最后一个大写字母
     * @param str
     * @return -1 表示没有
     */
    public static int lastUpperLetter(String str){
        int len = str.length();

        for(int i = len-1; i>=0; i--){
            char c = str.charAt(i);
            if(CharUtil.isLetterUpper(c)){
                return  i;
            }
        }
        return -1;
    }



    /**
     * 时间区间 逗号分割 ,2022-10-11
     *
     * @param date 时间区间
     *
     * @return 区间对象
     *
     */
    public static Between parseBetween(String date) {
        Between between = new Between();
        if (StringUtils.isNotBlank(date)) {
            String[] s = date.split(",");
            if (s.length >= 1) {
                between.setBegin(s[0]);
            }
            if (s.length >= 2) {
                between.setEnd(s[1]);
            }
        }
        return between;
    }

    /**
     * 判断是否包含中文
     * @param text
     * @return
     */
    public static boolean hasChinese(String text) {
        if (text == null) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // 检查是否在基本汉字范围内
            if (c >= '\u4e00' && c <= '\u9fff') {
                return true;
            }
        }
        return false;

    }


    public static boolean anyContains(List<String> list, String search) {
        for (String str : list) {
            if (str.contains(search)) {
                return true;
            }
        }

        return false;
    }

    public static boolean anyContains(List<String> list, String... search) {
        for (String str : list) {
            for(String searchItem: search){
                if (str.contains(searchItem)) {
                    return true;
                }
            }

        }

        return false;

    }



    public static String removePreAndLowerFirst(String str, String prefix) {
        str = removePrefix(str,prefix);
        return str.substring(0,1).toLowerCase() + str.substring(1);
    }

    public static String removePrefix(String str, String prefix) {
        if (isEmpty(str) || isEmpty(prefix) || !str.startsWith(prefix)) {
            return str;
        }

        return str.substring(prefix.length());
    }


    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-","");
    }

    public static String toUnderlineCase(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }


}
