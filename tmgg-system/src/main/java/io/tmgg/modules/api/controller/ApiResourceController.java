package io.tmgg.modules.api.controller;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.service.ApiResourceService;
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
        JpaQuery<ApiResource> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), ApiResource.Fields.name, ApiResource.Fields.action, ApiResource.Fields.desc);
        Page<ApiResource> page = apiResourceService.findAll(pageable);

        return AjaxResult.ok().data(page);
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<ApiResource> list = apiResourceService.findAll();

        List<Option> options = Option.convertList(list, ApiResource::getAction, ApiResource::getName);

        return AjaxResult.ok().data(options);
    }
}
