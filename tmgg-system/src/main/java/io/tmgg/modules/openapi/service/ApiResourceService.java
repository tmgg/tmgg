package io.tmgg.modules.openapi.service;

import cn.hutool.core.collection.CollUtil;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.openapi.dao.OpenApiResourceDao;
import io.tmgg.modules.openapi.entity.OpenApiResource;
import io.tmgg.modules.openapi.entity.OpenApiResourceArgument;
import io.tmgg.modules.openapi.entity.OpenApiResourceArgumentReturn;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiResourceService extends BaseService<OpenApiResource> {

    @Resource
    private OpenApiResourceDao dao;

    private final Map<String, Method> map = new HashMap<>();

    public Method findMethodByAction(String action) {
        return map.get(action);
    }
    public OpenApiResource findByAction(String action) {
        JpaQuery<OpenApiResource> q = new JpaQuery<>();
        q.eq(OpenApiResource.Fields.action, action);
        return this.findOne(q);

    }
    public List<OpenApiResource> findAll() {
        return dao.findAll(Sort.by(OpenApiResource.Fields.action));
    }


    @Transactional
    public void add(OpenApiResource r) {
        List<OpenApiResourceArgumentReturn> returnList = r.getReturnList();
        List<OpenApiResourceArgument> parameterList = r.getParameterList();

        if (CollUtil.isNotEmpty(returnList)) {
            for (OpenApiResourceArgumentReturn a : returnList) {
                a.setResource(r);
            }
        }

        if (CollUtil.isNotEmpty(parameterList)) {
            for (OpenApiResourceArgument a : parameterList) {
                a.setResource(r);
            }
        }

        dao.save(r);
        map.put(r.getAction(), r.getMethod());
    }


}
