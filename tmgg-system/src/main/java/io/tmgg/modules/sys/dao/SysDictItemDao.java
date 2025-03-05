
package io.tmgg.modules.sys.dao;

import io.tmgg.framework.cache.EhCacheService;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.entity.SysDict;
import io.tmgg.modules.sys.entity.SysDictItem;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.ehcache.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统字典值
 *
 *
 */
@Repository
@CacheConfig(cacheNames = "dict")
public class SysDictItemDao extends BaseDao<SysDictItem> {




    @CacheEvict(allEntries = true)
    @Transactional
    public void deleteByPid(String typeId) {
        JpaQuery<SysDictItem> q = new JpaQuery<>();

        q.eq(SysDictItem.Fields.sysDict + ".id", typeId);

        List<SysDictItem> list = this.findAll(q);


        this.deleteAll(list);
    }

    @Cacheable
    public String findText(String typeCode, String itemCode) {
        JpaQuery<SysDictItem> q = new JpaQuery<>();
        q.eq(SysDictItem.Fields.sysDict + "." + SysDict.Fields.code, typeCode);
        q.eq(SysDictItem.Fields.code, itemCode);

        SysDictItem item = this.findOne(q);
        if(item != null){
            String text = item.getText();
            return text;
        }
        return null;
    }

    @CacheEvict(allEntries = true)
    @Override
    public SysDictItem save(SysDictItem entity) {
        return super.save(entity);
    }

    public List<SysDictItem> findAllByDictCode(String code) {
        JpaQuery<SysDictItem> q = new JpaQuery<>();
        q.eq(SysDictItem.Fields.sysDict + "." + SysDict.Fields.code, code);
        return this.findAll(q);
    }
}
