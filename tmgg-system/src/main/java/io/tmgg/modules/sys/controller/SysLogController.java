
package io.tmgg.modules.sys.controller;

import cn.hutool.core.date.DateUtil;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysLog;
import io.tmgg.modules.sys.service.SysLogService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("sysLog")
public class SysLogController {


    @Resource
    private SysLogService sysOpLogService;

    @Data
    public static class QueryParam {
        String dateRange;
        String name;
        String module;
    }



    @HasPermission(log = false)
    @RequestMapping("page")
    public AjaxResult page(@RequestBody QueryParam queryParam, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        String dateRange = queryParam.getDateRange();

        JpaQuery<SysLog> q = new JpaQuery<>();
        q.betweenIsoDateRange("createTime", dateRange);
        q.like(SysLog.Fields.name, queryParam.getName());
        q.like(SysLog.Fields.module, queryParam.getModule());

        Page<SysLog> page = sysOpLogService.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


}
