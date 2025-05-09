package io.tmgg.modules.api.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.Table;
import io.tmgg.modules.api.Api;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.service.ApiResourceService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(sort = "uri") Pageable pageable) throws Exception {
        JpaQuery<ApiResource> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), ApiResource.Fields.name, ApiResource.Fields.uri, ApiResource.Fields.desc);
        Page<ApiResource> page = apiResourceService.findAll(pageable);

        return AjaxResult.ok().data(page);
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<ApiResource> list = apiResourceService.findAll();

        List<Option> options = Option.convertList(list, ApiResource::getUri, ApiResource::getName);

        return AjaxResult.ok().data(options);
    }

    @GetMapping("tableSelect")
    public AjaxResult tableSelect(String searchText, String selectedKey, Pageable pageable) {
        JpaQuery<ApiResource> q = new JpaQuery<>();
        q.searchText(searchText, ApiResource.Fields.name, ApiResource.Fields.uri, ApiResource.Fields.desc);

        if(StrUtil.isNotEmpty(selectedKey)){
          q.eq("id", selectedKey);
        }

        Page<ApiResource> page = apiResourceService.findAll(q,pageable);



        Table<ApiResource> tb = new Table<>(page);
        tb.addColumn("标识", "id");
        tb.addColumn("名称", ApiResource.Fields.name);
        tb.addColumn("路径", ApiResource.Fields.uri);
        tb.addColumn("描述", ApiResource.Fields.desc);





        return AjaxResult.ok().data(tb);
    }
}
