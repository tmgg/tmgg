package io.tmgg.modules.asset.service;

import io.tmgg.modules.asset.entity.SysAssetDir;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.stereotype.Service;

@Service
public class SysAssetDirService extends BaseService<SysAssetDir> {

    public SysAssetDir findByCode(String code) {
        JpaQuery<SysAssetDir> q = new JpaQuery<>();
        q.eq("code",code);
        return baseDao.findOne(q);
    }
}

