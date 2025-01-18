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
        Map<String, List<Object>> keyedMapList = complexResult.getKeyedMapList();

        for (Map.Entry<String, List<Object>> e : keyedMapList.entrySet()) {
            List<Object> valueList = e.getValue();
            for (int i = 0; i < valueList.size(); i++) {
                Object v = valueList.get(i);
                if (v instanceof Boolean) {
                    v = BooleanUtil.toStringTrueFalse((Boolean) v);
                }
                valueList.set(i, v);
            }
        }

        return complexResult;
    }
}
