package io.tmgg.web.db;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DbCacheDao extends BaseDao<DbCache> {

    @Transactional
    public String findStrByCode(String code){
        DbCache cache = this.findOneByField(DbCache.Fields.code, code);
        if(cache != null){
            return cache.getValue();
        }
        return null;
    }

    public Map<String,String> findDictByCodePrefix(String code){
        JpaQuery<DbCache> q = new JpaQuery<>();
        q.like(DbCache.Fields.code, code + "%");

        List<DbCache> list = this.findAll();
        Map<String,String> map = new HashMap<>();
        for (DbCache dbCache : list) {
            map.put(dbCache.getCode(), dbCache.getValue());
        }
        return map;
    }


    @Transactional
    public DbCache save(String code, String value){
        Assert.notNull(value,"值不能为空，如需删除，请使用delete方法");
        DbCache dbCache = new DbCache();
        dbCache.setCode(code);
        dbCache.setValue(value);
        return this.save(dbCache);
    }

    @Transactional
    public void deleteByCode(String code){
        DbCache cache = this.findOneByField(DbCache.Fields.code, code);
        if(cache != null){
           this.delete(cache);
        }
    }
}
