package io.tmgg.modules.job.controller;

import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.service.SysJobLogService;
import io.tmgg.modules.job.service.SysJobLogFileService;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;


@RestController
@RequestMapping("job/log/print")
public class SysJobLogPrinterController {

    @Resource
    SysJobLogFileService service;

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
                w.println("任务Id：" + latest.getId());
            } else {
                w.println("任务尚未执行");
            }
        }

        service.read(jobLogId, response.getWriter());

    }


}
