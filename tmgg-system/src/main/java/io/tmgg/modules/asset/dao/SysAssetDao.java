package io.tmgg.modules.asset.dao;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.asset.entity.SysAsset;
import io.tmgg.web.persistence.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class SysAssetDao extends BaseDao<SysAsset> {

    public SysAsset findByName(String name){
        JpaQuery<SysAsset> q = new JpaQuery<>();
        q.eq(SysAsset.Fields.name, name);
        return this.findOne(q);
    }

}

