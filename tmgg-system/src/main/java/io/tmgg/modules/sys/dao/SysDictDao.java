
package io.tmgg.modules.sys.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.entity.SysDict;
import org.springframework.stereotype.Repository;

@Repository
public class SysDictDao extends BaseDao<SysDict> {

    public SysDict findByCode(String code) {
        JpaQuery<SysDict> q = new JpaQuery<>();
        q.eq(SysDict.Fields.code, code);
        return this.findOne(q);
    }
    public boolean existsByCode(String code) {
        JpaQuery<SysDict> q = new JpaQuery<>();
        q.eq("code",code);
        return this.exists(q);
    }
}
