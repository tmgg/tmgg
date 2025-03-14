
package io.tmgg.modules.sys.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.DateRange;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.service.SysOpLogService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysLog;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;


@RestController
public class SysOpLogController {


    @Resource
    private SysOpLogService sysOpLogService;

    @Data
    public static class QueryParam {
        DateRange dateRange;
        String name;
        String module;
    }


    @HasPermission(log = false)
    @RequestMapping("/sysOpLog/page")
    public AjaxResult opLogPage(@RequestBody QueryParam queryParam, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        DateRange dateRange = queryParam.getDateRange();

        JpaQuery<SysLog> q = new JpaQuery<>();
        if (dateRange != null && dateRange.isNotEmpty()) {
            q.between("createTime", dateRange.getBegin(), dateRange.getEnd());
        }

        q.like(SysLog.Fields.name, queryParam.getName());
        q.like(SysLog.Fields.module, queryParam.getModule());

        Page<SysLog> page = sysOpLogService.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


}
