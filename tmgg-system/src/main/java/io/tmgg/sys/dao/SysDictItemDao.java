
package io.tmgg.sys.dao;

import io.tmgg.framework.cache.EhCacheTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.entity.SysDict;
import io.tmgg.sys.entity.SysDictItem;
import org.ehcache.Cache;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

/**
 * 系统字典值
 *

 *
 */
@Repository
public class SysDictItemDao extends BaseDao<SysDictItem> {
   private Cache<String, String> cache = EhCacheTool.create("dict", String.class, String.class, 100, Duration.ofHours(1));


    @Transactional
    public void deleteByTypeId(String typeId) {
        JpaQuery<SysDictItem> q = new JpaQuery<>();

        q.eq(SysDictItem.Fields.sysDict + ".id", typeId);

        List<SysDictItem> all = this.findAll(q);


        this.deleteAll(all);
    }

    public String findTextByDictCodeAndKey(String code, String key) {
        String uid = code + ":" + key;
        if(cache.containsKey(uid)){
            return cache.get(uid);
        }


        JpaQuery<SysDictItem> q = new JpaQuery<>();
        q.eq(SysDictItem.Fields.sysDict + "." + SysDict.Fields.code, code);
        q.eq(SysDictItem.Fields.code, key);

        SysDictItem item = this.findOne(q);
        if(item != null){
            String text = item.getText();
            cache.put(uid, text);
            return text;
        }
        return null;
    }

    public List<SysDictItem> findAllByDictCode(String code) {
        JpaQuery<SysDictItem> q = new JpaQuery<>();
        q.eq(SysDictItem.Fields.sysDict + "." + SysDict.Fields.code, code);
        return this.findAll(q);
    }
}
