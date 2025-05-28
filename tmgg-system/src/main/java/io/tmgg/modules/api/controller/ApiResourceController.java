package io.tmgg.modules.api.controller;

import cn.hutool.core.collection.CollUtil;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.table.Table;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.service.ApiResourceService;
import io.tmgg.web.pojo.param.DropdownParam;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("apiResource")
public class ApiResourceController extends BaseController<ApiResource> {
    @Resource
    private ApiResourceService service;



    @RequestMapping("tableSelect")
    public AjaxResult tableSelect(DropdownParam param, Pageable pageable) {
        JpaQuery<ApiResource> q = new JpaQuery<>();
        q.searchText(param.getSearchText(), ApiResource.Fields.name, ApiResource.Fields.uri, ApiResource.Fields.desc);

        List<String> selected = param.getSelected();
        if(CollUtil.isNotEmpty(selected)){
          q.in("id", selected);
        }

        Page<ApiResource> page = service.findAll(q,pageable);



        Table<ApiResource> tb = new Table<>(page);
        tb.addColumn("标识", "id");
        tb.addColumn("名称", ApiResource.Fields.name).setSorter(true);
        tb.addColumn("路径", ApiResource.Fields.uri).setSorter(true);
        tb.addColumn("描述", ApiResource.Fields.desc);

        return AjaxResult.ok().data(tb);
    }
}
