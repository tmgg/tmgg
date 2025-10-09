package io.tmgg.modules.api.service;

import io.tmgg.web.persistence.BaseService;
import io.tmgg.modules.api.dao.ApiResourceDao;
import io.tmgg.modules.api.entity.ApiResource;
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
    private ApiResourceDao dao;


    private final Map<String, Method> pathBindings = new HashMap<>();

    public Method findMethodByAction(String action) {
        return pathBindings.get(action);
    }

    public List<ApiResource> findAll() {
        return dao.findAll(Sort.by(ApiResource.Fields.path));
    }


    @Transactional
    public void add(ApiResource r) {
        dao.save(r);
        pathBindings.put(r.getPath(), r.getMethod());
    }


    public List<ApiResource> removeNotExist(List<ApiResource> list) {
        return list.stream().filter(t->pathBindings.containsKey(t.getPath())).toList();
    }
}
