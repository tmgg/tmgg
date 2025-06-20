
package io.tmgg.modules.system.controller;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.entity.SysLog;
import io.tmgg.modules.system.service.SysLogService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("sysLog")
public class SysLogController {


    @Resource
    private SysLogService service;

    @Data
    public static class QueryParam {
        String dateRange;
        String name;
        String module;
    }



    @HasPermission(log = false)
    @RequestMapping("page")
    public AjaxResult page( QueryParam queryParam, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {
        String dateRange = queryParam.getDateRange();

        JpaQuery<SysLog> q = new JpaQuery<>();
        q.betweenIsoDateRange("createTime", dateRange);
        q.like(SysLog.Fields.name, queryParam.getName());
        q.like(SysLog.Fields.module, queryParam.getModule());

        Page<SysLog> page = service.findAll(q, pageable);
        return service.autoRender(page);
    }


}
