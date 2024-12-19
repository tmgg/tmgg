package io.tmgg.job.controller;

import io.tmgg.job.entity.SysJobLog;
import io.tmgg.job.entity.SysJobLogging;
import io.tmgg.job.service.SysJobLogService;
import io.tmgg.job.service.SysJobLoggingService;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Date;


@RestController
@RequestMapping("job/log/print")
public class SysJobLogPrinterController {

    @Resource
    SysJobLoggingService service;

    @Resource
    SysJobLogService sysJobLogService;


    @GetMapping
    public void log(String jobId, String jobLogId, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/plain; utf-8");
        PrintWriter w = response.getWriter();

        if (StringUtils.isNotEmpty(jobId)) {
            SysJobLog latest = sysJobLogService.findLatest(jobId);
            if (latest != null) {
                jobLogId = latest.getId();
                w.println("任务名称：" + latest.getSysJob().getName());
            } else {
                w.println("任务尚未执行");
            }
        }


        Page<SysJobLogging> page = service.read(jobLogId);

        if (page.hasNext()) {
            w.println("日志未显示全，只显示" + page.getPageable().getPageSize() + "条");
        }


        for (SysJobLogging jl : page) {
            String line = "%s %s".formatted(DateUtil.formatDateTime(jl.getCreateTime()), jl.getMessage());
            w.println(line);
        }


    }


}
