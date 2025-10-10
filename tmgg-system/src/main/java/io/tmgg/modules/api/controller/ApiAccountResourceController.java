package io.tmgg.modules.api.controller;

import io.tmgg.web.WebConstants;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.entity.ApiAccountResource;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import io.tmgg.web.persistence.BaseController;


import io.tmgg.web.annotion.HasPermission;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

import java.util.Map;

@RestController
@RequestMapping("apiAccountResource")
public class ApiAccountResourceController {

    @Resource
    ApiAccountResourceService service;

    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(String searchText, @PageableDefault(sort = "action") Pageable pageable) throws Exception {
        JpaQuery<ApiAccountResource> q = new JpaQuery<>();
        q.searchText(searchText, service.getSearchableFields());

        Page<ApiAccountResource> page = service.findAllByClient(q, pageable);

        return AjaxResult.ok().data(page);
    }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody ApiAccountResource input, RequestBodyKeys updateFields) throws Exception {
        service.saveOrUpdateByClient(input, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }


    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteByClient(id);
        return AjaxResult.ok().msg("删除成功");
    }


}

