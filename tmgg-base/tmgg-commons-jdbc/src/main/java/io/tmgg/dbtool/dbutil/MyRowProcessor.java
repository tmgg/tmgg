package io.tmgg.dbtool.dbutil;

import cn.hutool.core.map.CaseInsensitiveLinkedMap;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyRowProcessor extends BasicRowProcessor {

    boolean namingStrategyImproved;

    public MyRowProcessor(BeanProcessor convert, boolean NAMING_STRATEGY_IMPROVED) {
        super(convert);
        this.namingStrategyImproved = NAMING_STRATEGY_IMPROVED;
    }

    @Override
    public Map<String, Object> toMap(ResultSet resultSet) throws SQLException {
        Map<String, Object> map = super.toMap(resultSet);

        if(namingStrategyImproved){
            Map<String, Object> result = BasicRowProcessor.createCaseInsensitiveHashMap(map.size());
            for (Map.Entry<String, Object> e : map.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                key = StrUtil.toCamelCase(key);
                result.put(key, value);
            }
            return result;
        }
        return map;
    }
}
