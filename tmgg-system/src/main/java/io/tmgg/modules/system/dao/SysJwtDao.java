package io.tmgg.modules.system.dao;

import io.tmgg.data.repository.BaseDao;
import io.tmgg.data.query.JpaQuery;
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
