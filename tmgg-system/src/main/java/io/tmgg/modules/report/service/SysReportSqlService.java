package io.tmgg.modules.report.service;


import cn.hutool.core.util.BooleanUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.dbtool.obj.ComplexResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SysReportSqlService {

    @Resource
    DbTool db;

    public ComplexResult runSql(String sql) {
        ComplexResult complexResult = db.findComplexResult(sql);
        Map<String, Object[]> keyedMapList = complexResult.getKeyedMapList();

        for (Map.Entry<String, Object[]> e : keyedMapList.entrySet()) {
            Object[] valueList = e.getValue();
            for (int i = 0; i < valueList.length; i++) {
                Object v = valueList[i];
                if (v instanceof Boolean) {
                    v = BooleanUtil.toStringTrueFalse((Boolean) v);
                }
                valueList[i]= v;
            }
        }

        return complexResult;
    }
}
