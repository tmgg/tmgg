package io.tmgg.web.db;

import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Repository
public class DbRecordDao extends BaseDao<DbRecord> {

    @Transactional
    public String findStrByCode(String code){
        DbRecord cache = this.findOneByField(DbRecord.Fields.code, code);
        if(cache != null){
            return cache.getValue();
        }
        return null;
    }



    @Transactional
    public DbRecord save(String code, String value){
        Assert.notNull(value,"值不能为空，如需删除，请使用delete方法");
        DbRecord dbRecord = new DbRecord();
        dbRecord.setId(code);
        dbRecord.setCode(code);
        dbRecord.setValue(value);
        return this.save(dbRecord);
    }

    @Transactional
    public void deleteByCode(String code){
        DbRecord cache = this.findOneByField(DbRecord.Fields.code, code);
        if(cache != null){
           this.delete(cache);
        }
    }
}
