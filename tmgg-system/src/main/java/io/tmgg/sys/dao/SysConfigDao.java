
package io.tmgg.sys.dao;


import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.entity.SysConfig;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统参数配置 Mapper 接口
 */
@Repository
public class SysConfigDao extends BaseDao<SysConfig> {


    /**
     * 通过前缀查询键值对
     *
     * @param prefix
     * @return
     */
    public Map<String, Object> findByPrefix(String prefix) {
        if(!prefix.endsWith(".")){
            prefix = prefix +".";
        }
        JpaQuery<SysConfig> q = new JpaQuery<>();
        q.like("id", prefix + "%");
        List<SysConfig> list = this.findAll(q);

        Map<String, Object> map = new HashMap<>();
        for (SysConfig sysConfig : list) {
            String k = sysConfig.getId();
            k = k.replace(prefix, "");
            Object v = parseValue(sysConfig);
            map.put(k, v);
        }

        return map;
    }

    private static Object parseValue(SysConfig sysConfig) {
        String v = StrUtil.emptyToDefault(sysConfig.getValue(), sysConfig.getDefaultValue());
        if ("boolean".equals(sysConfig.getValueType())) {
            return Boolean.valueOf(v);
        }
        return v;
    }


    public Object findValue(String key) {
        SysConfig sysConfig = this.findOne(key);
        Assert.notNull(sysConfig, "系统配置不存在" + key);
        return parseValue(sysConfig);
    }

    public String findValueStr(String key) {
        SysConfig sysConfig = this.findOne(key);
        if (sysConfig != null) {
            return (String) parseValue(sysConfig);
        }
        return null;
    }
}
