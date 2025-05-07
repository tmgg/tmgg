
package io.tmgg.modules.sys.dao;


import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.event.SysConfigChangeEvent;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.modules.sys.entity.SysConfig;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * 系统参数配置
 */
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
}
