package io.tmgg.modules.system.service;

import io.tmgg.modules.system.dao.SysManualDao;
import io.tmgg.modules.system.entity.SysManual;
import io.tmgg.web.persistence.BaseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysManualService extends BaseService<SysManual> {

    @Resource
    SysManualDao dao;

    @Override
    public SysManual saveOrUpdate(SysManual input, List<String> updateKeys) throws Exception {
        if(input.isNew()){
            int maxVersion = dao.findMaxVersion(input.getName());
            input.setVersion(maxVersion+1);
        }

        return super.saveOrUpdate(input, updateKeys);
    }
}

