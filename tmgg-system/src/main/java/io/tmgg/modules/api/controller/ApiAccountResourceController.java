package io.tmgg.modules.api.controller;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.entity.ApiAccountResource;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.CommonQueryParam;


import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

@RestController
@RequestMapping("openApiAccountResource")
public class ApiAccountResourceController extends BaseController<ApiAccountResource>{

    @Resource
    ApiAccountResourceService service;


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  CommonQueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<ApiAccountResource> q = new JpaQuery<>();

        // 关键字搜索，请补全字段
        q.searchText(param.getKeyword(), "字段1","字段2");

        Page<ApiAccountResource> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


}

