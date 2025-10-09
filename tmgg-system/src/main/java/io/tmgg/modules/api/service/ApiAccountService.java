package io.tmgg.modules.api.service;

import io.tmgg.modules.api.ApiClient;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.modules.api.dao.ApiAccountDao;
import io.tmgg.modules.api.entity.ApiAccount;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAccountService extends BaseService<ApiAccount> {

    @Resource
    ApiAccountDao apiAccountDao;

    public ApiAccount findByAppId(String appId) {
        JpaQuery<ApiAccount> q = new JpaQuery<>();
        q.eq(ApiAccount.Fields.appId, appId);
        return apiAccountDao.findOne(q);
    }
}
