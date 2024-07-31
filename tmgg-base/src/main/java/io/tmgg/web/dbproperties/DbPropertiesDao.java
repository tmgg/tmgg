package io.tmgg.web.dbproperties;

import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Repository
public class DbPropertiesDao extends BaseDao<DbProperties> {

    @Transactional
    public String findStrByCode(String code){
        DbProperties cache = this.findOneByField(DbProperties.Fields.code, code);
        if(cache != null){
            return cache.getValue();
        }
        return null;
    }



    @Transactional
    public DbProperties save(String code, String value){
        Assert.notNull(value,"值不能为空，如需删除，请使用delete方法");
        DbProperties dbProperties = new DbProperties();
        dbProperties.setId(code);
        dbProperties.setCode(code);
        dbProperties.setValue(value);
        return this.save(dbProperties);
    }

    @Transactional
    public void deleteByCode(String code){
        DbProperties cache = this.findOneByField(DbProperties.Fields.code, code);
        if(cache != null){
           this.delete(cache);
        }
    }
}
