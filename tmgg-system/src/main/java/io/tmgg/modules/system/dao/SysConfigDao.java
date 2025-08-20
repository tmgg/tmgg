
package io.tmgg.modules.system.dao;


import io.tmgg.event.SysConfigChangeEvent;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.system.entity.SysConfig;
import io.tmgg.web.persistence.BaseDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统参数配置
 */
@Slf4j
@Repository
@CacheConfig(cacheNames = "sys-config")
public class SysConfigDao extends BaseDao<SysConfig> {


    @CacheEvict(allEntries = true)
    @Override
    public SysConfig save(SysConfig entity) {
        SysConfig data = super.save(entity);
        SpringTool.publishEventAsync(new SysConfigChangeEvent(this, data.getId()));
        return data;
    }

    @Cacheable
    @Override
    public SysConfig findOne(String id) {
        return super.findOne(id);
    }


    @Transactional
    public void addDefault(String label, String id, String defaultValue) {
        this.addDefault(label, id, defaultValue, null);
    }

    @Transactional
    public void addDefault(String label, String id, String defaultValue, String valueType) {
        SysConfig cfg = super.findOne(id);
        if (cfg != null) {
            // 只更新无关痛痒的字段
            cfg.setValueType(valueType);
            cfg.setLabel(label);
            return;
        }
        cfg = new SysConfig();
        cfg.setId(id);
        cfg.setDefaultValue(defaultValue);
        cfg.setLabel(label);
        cfg.setValueType(valueType);
        this.save(cfg);
    }

    @CacheEvict(allEntries = true)
    public void cleanCache() {
        log.info("清空系统配置缓存");
    }
}
