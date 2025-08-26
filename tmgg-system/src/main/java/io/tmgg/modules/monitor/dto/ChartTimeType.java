package io.tmgg.modules.monitor.dto;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public enum ChartTimeType {
    HOUR_1,
    HOUR_24,

    DAY_7,
    DAY_30;

    public Date getBegin(){
        Date now = new Date();
        switch (this) {
            case HOUR_1 -> {
                return DateUtil.offsetHour(now, -1);
            }
            case HOUR_24 -> {
                return DateUtil.offsetHour(now, -24);
            }
            case DAY_7 -> {
                return DateUtil.offsetDay(now, -7);
            }
            case DAY_30 -> {
                return DateUtil.offsetDay(now, -30);
            }
        }
        return  null;
    }



}
