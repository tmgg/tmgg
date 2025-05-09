package io.tmgg.modules.api.service;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.api.entity.ApiAccount;
import io.tmgg.modules.api.entity.ApiAccountResource;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.api.entity.ApiResource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiAccountResourceService extends BaseService<ApiAccountResource> {

    public List<ApiResource> findByAccount(ApiAccount acc) {
        JpaQuery<ApiAccountResource> q = new JpaQuery<>();
        q.eq(ApiAccountResource.Fields.account, acc);

        List<ApiAccountResource> list = this.findAll(q);


        List<ApiResource> resourceList = list.stream().map(ApiAccountResource::getResource)
                .sorted(Comparator.comparing(ApiResource::getUri))
                .collect(Collectors.toList());

        return resourceList;
    }

    public ApiAccountResource findByAccountAndUri(ApiAccount account, String uri) {
        JpaQuery<ApiAccountResource> q = new JpaQuery<>();
        q.eq(ApiAccountResource.Fields.account, account);
        q.eq(ApiAccountResource.Fields.resource + "." + ApiResource.Fields.uri, uri);

        return this.findOne(q);

    }
}

