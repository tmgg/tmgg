package io.tmgg.modules.system.dao;

import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.system.entity.SysJwt;
import org.springframework.stereotype.Repository;

@Repository
public class SysJwtDao extends BaseDao<SysJwt> {

    public SysJwt findByTokenMd5(String tokenMd5){
        JpaQuery<SysJwt> q = new JpaQuery<>();
        q.eq(SysJwt.Fields.tokenMd5, tokenMd5);
        return findOne(q);
    }
}
