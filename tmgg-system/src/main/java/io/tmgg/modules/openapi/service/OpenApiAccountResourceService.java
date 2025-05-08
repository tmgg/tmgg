package io.tmgg.modules.openapi.service;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.openapi.entity.OpenApiAccount;
import io.tmgg.modules.openapi.entity.OpenApiAccountResource;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.openapi.entity.OpenApiResource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenApiAccountResourceService extends BaseService<OpenApiAccountResource> {

    public List<OpenApiResource> findByAccount(OpenApiAccount acc) {
        JpaQuery<OpenApiAccountResource> q = new JpaQuery<>();
        q.eq(OpenApiAccountResource.Fields.account, acc);

        List<OpenApiAccountResource> list = this.findAll(q);


        List<OpenApiResource> resourceList = list.stream().map(OpenApiAccountResource::getResource)
                .sorted(Comparator.comparing(OpenApiResource::getAction))
                .collect(Collectors.toList());

        return resourceList;
    }

    public OpenApiAccountResource findByAccountAndAction(OpenApiAccount account, String action) {
        JpaQuery<OpenApiAccountResource> q = new JpaQuery<>();
        q.eq(OpenApiAccountResource.Fields.account, account);
        q.eq(OpenApiAccountResource.Fields.resource + "." + OpenApiResource.Fields.action, action);

        return this.findOne(q);

    }
}

