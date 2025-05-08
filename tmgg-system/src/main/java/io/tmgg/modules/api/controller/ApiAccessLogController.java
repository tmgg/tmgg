package io.tmgg.modules.api.controller;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.api.entity.ApiAccessLog;
import io.tmgg.modules.api.service.ApiAccessLogService;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.web.CommonQueryParam;


import io.tmgg.web.annotion.HasPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;
import java.util.List;
import java.io.IOException;
@RestController
@RequestMapping("apiAccessLog")
public class ApiAccessLogController  extends BaseController<ApiAccessLog>{

    @Resource
    ApiAccessLogService service;


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  CommonQueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<ApiAccessLog> q = new JpaQuery<>();

        // 关键字搜索，请补全字段
        q.searchText(param.getKeyword(), "字段1","字段2");

        Page<ApiAccessLog> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


}

