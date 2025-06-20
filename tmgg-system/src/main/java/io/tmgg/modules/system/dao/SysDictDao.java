
package io.tmgg.modules.system.dao;

import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.system.entity.SysDict;
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

    public SysDict findByIdOrCode(String code) {
        JpaQuery<SysDict> q = new JpaQuery<>();
        q.addSubOr(qq->{
            qq.eq(SysDict.Fields.code, code);
            qq.eq("id", code);
        });

        return this.findOne(q);
    }


}
