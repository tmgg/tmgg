
package io.tmgg.sys.dao;


import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.entity.SysConfig;
import org.springframework.stereotype.Repository;

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
        JpaQuery<SysConfig> q = new JpaQuery<>();
        q.like("id", prefix + ".%");
        List<SysConfig> list = this.findAll(q);

        Map<String, Object> map = new HashMap<>();
        for (SysConfig sysConfig : list) {
            String k = sysConfig.getId();
            String v = parseValue(sysConfig);

            map.put(k, v);
        }

        return map;
    }

    private static String parseValue(SysConfig sysConfig) {
        return StrUtil.emptyToDefault(sysConfig.getValue(), sysConfig.getDefaultValue());
    }


    public String findValue(String key) {
        SysConfig sysConfig = this.findOne(key);
        if (sysConfig != null) {
            return parseValue(sysConfig);
        }
        return null;
    }


}
