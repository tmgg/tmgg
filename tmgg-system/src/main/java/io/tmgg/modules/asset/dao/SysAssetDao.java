package io.tmgg.modules.asset.dao;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.asset.entity.SysAsset;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class SysAssetDao extends BaseDao<SysAsset> {

    public SysAsset findByCode(String code){
        JpaQuery<SysAsset> q = new JpaQuery<>();
        q.eq(SysAsset.Fields.code, code);
        return this.findOne(q);
    }

}

