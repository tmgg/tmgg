package io.tmgg.modules.report.controller;

import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.report.entity.SysReportChart;
import io.tmgg.modules.report.service.SysReportChartService;
import io.tmgg.modules.report.service.SysReportSqlService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("sysReportChart")
public class SysReportChartController extends BaseController<SysReportChart> {

    @Resource
    private SysReportChartService service;

    @Resource
    private SysReportSqlService sysReportSqlService;



    private JpaQuery<SysReportChart> buildQuery(CommonQueryParam param) {
        JpaQuery<SysReportChart> q = new JpaQuery<>();
       q.searchText(param.getKeyword(), "title","code");
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<SysReportChart> q = buildQuery(param);

        Page<SysReportChart> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


    @HasPermission
    @PostMapping("preview")
    public AjaxResult preview(@RequestBody SysReportChart param) throws Exception {
        String sql = param.getSql();
        Assert.hasText(sql, "请填写sql");
        SysReportChart chart = service.findOne(param.getId());
        Map<String, Object> option = service.buildEchartsOption(chart.getTitle(), param.getType(), sysReportSqlService.runSql(sql));

        return AjaxResult.ok().data(option);
    }

    @HasPermission
    @PostMapping("viewData")
    public AjaxResult viewData(@RequestBody SysReportChart param) throws Exception {
        String sql = param.getSql();
        Assert.hasText(sql, "请填写sql");

        return AjaxResult.ok().data(sysReportSqlService.runSql(sql));
    }

    @GetMapping("get")
    public AjaxResult get(String id) throws Exception {
        SysReportChart chart = service.findOne(id);

        return AjaxResult.ok().data(chart);
    }

    @HasPermission(value = "sysChart:view",label = "查看报表")
    @GetMapping("getOption/{code}")
    public AjaxResult draw(@PathVariable String code) throws Exception {
        SysReportChart chart = service.findByCode(code);
        Map<String, Object> option = service.buildEchartsOption(chart.getTitle(), chart.getType(), sysReportSqlService.runSql(chart.getSql()));

        service.addViewCount(code);
        return AjaxResult.ok().data(option);
    }

    @HasPermission(value = "sysChart:view",label = "查看报表")
    @GetMapping("view/{code}")
    public void view(@PathVariable String code, HttpServletResponse response) throws Exception {
        SysReportChart chart = service.findByCode(code);
        Map<String, Object> option = service.buildEchartsOption(chart.getTitle(), chart.getType(), sysReportSqlService.runSql(chart.getSql()));
        String json = JsonTool.toPrettyJsonQuietly(option);
        service.addViewCount(code);
        String html = """
                <!DOCTYPE html>
                <html>
                 <head>
                   <meta charset="utf-8" />
                   <script src="https://cdnjs.cloudflare.com/ajax/libs/echarts/5.6.0/echarts.min.js"></script>
                 </head>
                 <body>
                 <div id='main' style="width: 100%;height: 100vh"></div>
                 <script>
                 var myChart = echarts.init(document.getElementById('main'));
                 // 绘制图表
                 myChart.setOption(OPTION);
                 </script>
                 </body>
                </html>
                                """;

        response.setContentType("text/html; charset=utf-8");
        response.getWriter().println(html.replace("OPTION", json));
    }

}

