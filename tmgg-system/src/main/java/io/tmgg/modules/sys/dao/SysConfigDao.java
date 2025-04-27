
package io.tmgg.modules.sys.dao;


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
        return super.save(entity);
    }

    @Cacheable
    @Override
    public SysConfig findOne(String id) {
        return super.findOne(id);
    }
}
