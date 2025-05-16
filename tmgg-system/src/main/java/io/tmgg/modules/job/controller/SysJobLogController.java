package io.tmgg.modules.job.controller;

import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.service.SysJobLogService;
import io.tmgg.modules.job.service.SysJobService;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("job")
public class SysJobLogController {

    @Resource
    private SysJobLogService service;

    @Resource
    private SysJobService sysJobService;

    @RequestMapping("jobLog")
    public AjaxResult page(String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.searchText(searchText, SysJobLog.Fields.sysJob + "." + SysJob.Fields.name);

        Page<SysJobLog> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

    @Data
    public static class CleanParam {
        @NotEmpty
        List<String> ids;
    }

    @HasPermission(label = "清理日志")
    @PostMapping("jobLogClean")
    public AjaxResult clean(@RequestBody CleanParam param) {
        List<String> ids = param.getIds();
        sysJobService.clean(ids);
        return AjaxResult.ok().msg("删除成功");
    }


}
