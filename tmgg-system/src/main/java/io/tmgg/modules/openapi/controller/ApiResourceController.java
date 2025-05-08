package io.tmgg.modules.openapi.controller;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.openapi.entity.OpenApiResource;
import io.tmgg.modules.openapi.service.ApiResourceService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("openApiResource")
public class ApiResourceController {
    @Resource
    private ApiResourceService apiResourceService;
    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(sort = "action") Pageable pageable) throws Exception {
        JpaQuery<OpenApiResource> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), OpenApiResource.Fields.name, OpenApiResource.Fields.action, OpenApiResource.Fields.desc);
        Page<OpenApiResource> page = apiResourceService.findAll(pageable);

        return AjaxResult.ok().data(page);
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<OpenApiResource> list = apiResourceService.findAll();

        List<Option> options = Option.convertList(list, OpenApiResource::getAction, OpenApiResource::getName);

        return AjaxResult.ok().data(options);
    }
}
