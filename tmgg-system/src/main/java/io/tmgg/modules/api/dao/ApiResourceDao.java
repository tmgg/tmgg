package io.tmgg.modules.api.dao;

import io.tmgg.data.repository.BaseDao;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.data.query.JpaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class ApiResourceDao extends BaseDao<ApiResource> {

    public ApiResource findByAction(String action){
        JpaQuery<ApiResource> q = new JpaQuery<>();
        q.eq(ApiResource.Fields.action, action);
        return this.findOne(q);
    }

    public ApiResource findByName(String name){
        JpaQuery<ApiResource> q = new JpaQuery<>();
        q.eq(ApiResource.Fields.name, name);
        return this.findOne(q);
    }
}
