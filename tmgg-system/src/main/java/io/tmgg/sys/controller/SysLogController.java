
package io.tmgg.sys.controller;

import cn.hutool.core.date.DateUtil;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.service.SysOpLogService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.entity.SysLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.Date;

/**
 * 系统日志
 */
@RestController
public class SysLogController {


    @Resource
    private SysOpLogService sysOpLogService;



    @HasPermission
    @GetMapping("/sysOpLog/page")
    public AjaxResult opLogPage(String date, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {

        JpaQuery<SysLog> q = new JpaQuery<>();
        if(date!= null){
            Date d = DateUtil.parseDate(date);
            q.between("createTime", d,DateUtil.offsetDay(d,1));
        }



        Page<SysLog> page = sysOpLogService.findAll(q, pageable);
        return AjaxResult.ok().data(page).msg("查询操作日志成功");
    }




}
