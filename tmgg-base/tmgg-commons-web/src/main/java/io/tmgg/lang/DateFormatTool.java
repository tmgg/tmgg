package io.tmgg.lang;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class DateFormatTool {

    public static String format(Date date){
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDay(Date date){
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }
    public static String formatDayCn(Date date){
        return DateFormatUtils.format(date, "yyyy年M月d日");
    }


}
