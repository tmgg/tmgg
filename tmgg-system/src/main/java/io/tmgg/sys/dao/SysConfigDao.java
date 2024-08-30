
package io.tmgg.sys.dao;


import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.consts.SysConfigValueChangeEvent;
import io.tmgg.sys.entity.SysConfig;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统参数配置 Mapper 接口
 */
@Repository
@CacheConfig(cacheNames = "sys_config")
public class SysConfigDao extends BaseDao<SysConfig> {

    /**
     * 初始化配置， 如果不存在则添加
     *
     * @param code
     * @param name
     * @param defaultValue
     * @param remark
     */
    @Transactional
    public void initDefault(String code, String name, String defaultValue, String remark) {
        SysConfig config = this.findByCode(code);
        if (config == null) {
            config = new SysConfig();
            config.setCode(code);
            config.setName(name);
            config.setDefaultValue(defaultValue);
            config.setValue(defaultValue);
            config.setRemark(remark);
            this.save(config);
        }
    }


    @Cacheable
    public String findValueByCode(String code) {
        JpaQuery<SysConfig> query = new JpaQuery<>();
        query.eq(SysConfig.Fields.code, code);

        SysConfig config = this.findOne(query);
        if (config != null) {
            if (StrUtil.isEmpty(config.getValue())) {
                return config.getDefaultValue();
            }
            return config.getValue();
        }
        return null;
    }

    public SysConfig findByCode(String code) {
        JpaQuery<SysConfig> query = new JpaQuery<>();
        query.eq(SysConfig.Fields.code, code);

        SysConfig config = this.findOne(query);
        return config;
    }


    @CacheEvict(allEntries = true)
    @Override
    public SysConfig save(SysConfig entity) {
        String id = entity.getId();
        String oldValue = null;
        if (id != null) {
            SysConfig old = findOne(id);
            if (old != null) {
                oldValue = old.getValue();
            }

        }

        SysConfig newData = super.save(entity);


        String newValue = newData.getValue();
        if (!StringUtils.equals(oldValue, newValue)) {
            SysConfigValueChangeEvent event = new SysConfigValueChangeEvent(this);
            event.setCode(entity.getCode());
            event.setOldValue(oldValue);
            event.setNewValue(newValue);
            SpringTool.publishEvent(event);
        }

        return newData;
    }


    @CacheEvict(allEntries = true)
    public void clearCache() {
        // empty,  clear cache  trigger
    }


}
