package io.tmgg.modules.monitor.service;

import cn.hutool.core.date.DateUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.modules.monitor.dto.ChartResult;
import io.tmgg.modules.monitor.dto.ChartTimeType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SysMetricRecordService {

    @Resource
    DbTool db;


    public List<ChartResult> chart(String name, ChartTimeType type) {
        String sql = """
                SELECT create_time as createTime, metric_value as value , 'a,b,c' as test from sys_metric_record 
                where metric_name = ? and create_time between ? and ?
                """;
        Date begin = type.getBegin();
        Date end = new Date();
        List<ChartResult> list = db.findAll(ChartResult.class, sql, name, begin, end);


        for (ChartResult r : list) {
            r.setTime(DateUtil.formatDateTime(r.getCreateTime()));
        }




        return list;
    }
}
