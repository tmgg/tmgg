package io.tmgg.lang;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.dbtool.DbTool;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;


public class IdTool implements Serializable {

    private static final long startYear = DateUtil.parseDate("2000-01-01").getTime(); // 其实年


    /**
     * 基于时间的ID， 精确到秒, 分布式下慎用
     * 优点是
     *
     * @return id
     */
    public static synchronized int nextIdBySecondSlowly() {
        long id = (System.currentTimeMillis() - startYear) / 1000;

        return (int) id;
    }



    public static synchronized String nextIdByDb(String tableName, String prefix, int numLen) {
        int seq = getIdByDb(tableName, prefix) +1;
        String id = StringUtils.leftPad(String.valueOf(seq), numLen,'0');
        return prefix +id;
    }

    private static synchronized int getIdByDb(String tableName, String prefix) {
        int codeIndex = prefix.length() + 2; // mysql substr，
        String sql = "select max(CAST(SUBSTR(id,?) as signed)) as seq  from " + tableName + " where id like ?";
        Map<String, Object> map = SpringUtil.getBean(DbTool.class).findOne(sql, codeIndex, prefix + "%");
        Object seq = map.get("seq");
        if (seq == null) {
            return 0;
        }
        return Integer.valueOf(seq.toString());

    }



}
