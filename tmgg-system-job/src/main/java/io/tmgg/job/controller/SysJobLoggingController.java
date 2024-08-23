package io.tmgg.job.controller;

import io.tmgg.job.entity.SysJobLogging;
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
@RequestMapping("job/log/{name}")
public class SysJobLoggingController {

    @Resource
    SysJobLoggingService service;

    @GetMapping
    public void log(@PathVariable("name") String name, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/plain; utf-8");
        PrintWriter w = response.getWriter();

        w.println("注：日志使用 LoggerFactory.getLogger(\"job\") 或 Jobs.getLogger()");
        Page<SysJobLogging> page = service.read(name);

        if(page.hasNext()){
            w.println("日志未显示全，只显示" + page.getPageable().getPageSize() +"条");
        }

        Date d = new Date();
        for (SysJobLogging jl  :page){
            w.println(StringUtils.repeat("*", 80));
            d.setTime(jl.getTimeStamp());
            w.print(DateUtil.formatDateTime(d));
            w.print("  ");
            w.println(jl.getMessage());
            w.println(StringUtils.repeat("*", 80));
        }


    }


}
