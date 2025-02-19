package io.tmgg.modules.api.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.api.dao.ApiAccountDao;
import io.tmgg.modules.api.entity.ApiAccount;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAccountService extends BaseService<ApiAccount> {

    @Resource
    ApiAccountDao apiAccountDao;
    public ApiAccount findByAppKey(String appKey) {
        return apiAccountDao.findOneByField(ApiAccount.Fields.appKey, appKey);

    }
}
