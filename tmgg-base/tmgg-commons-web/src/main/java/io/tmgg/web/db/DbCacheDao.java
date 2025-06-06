package io.tmgg.web.db;

import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DbCacheDao extends BaseDao<DbCache> {

    public String get(String code) {
        DbCache cache = findByCode(code);
        if (cache != null) {
            return cache.getValue();
        }
        return null;
    }
    @Transactional
    public void set(String code, String value) {
        Assert.notNull(value, "值不能为空，如需删除，请使用delete方法");
        DbCache old = this.findByCode(code);
        if (old != null) {
            old.setValue(value);
            this.save(old);
        } else {
            DbCache dbCache = new DbCache();
            dbCache.setCode(code);
            dbCache.setValue(value);
            this.save(dbCache);
        }
    }

    public int getInt(String code){
        String v = get(code);
        return  v == null ? -1: Integer.parseInt(v);
    }

    @Transactional
    public void setInt(String code, int v){
        this.set(code,String.valueOf(v));
    }



    private DbCache findByCode(String code) {
        JpaQuery<DbCache> q = new JpaQuery<>();
        q.eq(DbCache.Fields.code, code);
       return this.findOne(q);
    }

    public Map<String, String> findDictByPrefix(String code) {
        JpaQuery<DbCache> q = new JpaQuery<>();
        q.like(DbCache.Fields.code, code + "%");

        List<DbCache> list = this.findAll();
        Map<String, String> map = new HashMap<>();
        for (DbCache dbCache : list) {
            map.put(dbCache.getCode(), dbCache.getValue());
        }
        return map;
    }

    @Transactional
    public  void cleanByPrefix(String code) {
        JpaQuery<DbCache> q = new JpaQuery<>();
        q.like(DbCache.Fields.code, code + "%");

        List<DbCache> list = this.findAll();

        this.deleteAll(list);
    }






    @Transactional
    public void deleteByCode(String code) {
        DbCache cache = findByCode(code);
        if (cache != null) {
            this.delete(cache);
        }
    }
}
