package io.tmgg.modules.api.service;

import cn.hutool.core.collection.CollUtil;
import io.tmgg.persistence.BaseService;
import io.tmgg.modules.api.dao.OpenApiResourceDao;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.entity.ApiResourceArgument;
import io.tmgg.modules.api.entity.ApiResourceArgumentReturn;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiResourceService extends BaseService<ApiResource> {

    @Resource
    private OpenApiResourceDao dao;

    private final Map<String, Method> map = new HashMap<>();

    public Method findMethodByAction(String action) {
        return map.get(action);
    }

    public List<ApiResource> findAll() {
        return dao.findAll(Sort.by(ApiResource.Fields.uri));
    }


    @Transactional
    public void add(ApiResource r) {
        List<ApiResourceArgumentReturn> returnList = r.getReturnList();
        List<ApiResourceArgument> parameterList = r.getParameterList();

        if (CollUtil.isNotEmpty(returnList)) {
            for (ApiResourceArgumentReturn a : returnList) {
                a.setResource(r);
            }
        }

        if (CollUtil.isNotEmpty(parameterList)) {
            for (ApiResourceArgument a : parameterList) {
                a.setResource(r);
            }
        }

        dao.save(r);
        map.put(r.getUri(), r.getMethod());
    }


}
