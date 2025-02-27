package io.tmgg.modules.openapi.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.openapi.dao.ApiAccountDao;
import io.tmgg.modules.openapi.entity.OpenApiAccount;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiAccountService extends BaseService<OpenApiAccount> {

    @Resource
    ApiAccountDao apiAccountDao;

}
