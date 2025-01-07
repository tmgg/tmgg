
package io.tmgg.modules.sys.controller;

import io.tmgg.lang.DateRange;
import io.tmgg.lang.ann.Remark;
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


@Remark("操作日志")
@RestController
public class SysOpLogController {


    @Resource
    private SysOpLogService sysOpLogService;

    @Data
    public static class QueryParam {
        DateRange dateRange;
    }


    @Remark("查询")
    @HasPermission
    @RequestMapping("/sysOpLog/page")
    public AjaxResult opLogPage(@RequestBody QueryParam queryParam, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        DateRange dateRange = queryParam.getDateRange();

        JpaQuery<SysLog> q = new JpaQuery<>();
        if(dateRange!= null && dateRange.isNotEmpty()){
            q.between("createTime", dateRange.getBegin(), dateRange.getEnd());
        }

        Page<SysLog> page = sysOpLogService.findAll(q, pageable);
        return AjaxResult.ok().data(page).msg("查询操作日志成功");
    }




}
