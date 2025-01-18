package io.tmgg.modules.report.controller;

import io.tmgg.dbtool.obj.ComplexResult;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.report.entity.SysReportChart;
import io.tmgg.modules.report.entity.SysReportTable;
import io.tmgg.modules.report.service.SysReportChartService;
import io.tmgg.modules.report.service.SysReportSqlService;
import io.tmgg.modules.report.service.SysReportTableService;
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
@RequestMapping("sysReportTable")
public class SysReportTableController extends BaseController<SysReportTable> {

    @Resource
    private SysReportTableService service;

    @Resource
    private SysReportSqlService sysReportSqlService;

    @Data
    public static class QueryParam {

        // 仅有一个搜索框时的搜索文本
        private String keyword;

        private String name;

        private String sqlContent;

        private String category;
    }

    private JpaQuery<SysReportTable> buildQuery(QueryParam param) {
        JpaQuery<SysReportTable> q = new JpaQuery<>();
        /*
            DateRange dateRange = param.getDateRange();
            if(dateRange != null && dateRange.isNotEmpty()){
                q.between(PointsItemRecord.Fields.redeemTime, dateRange.getBegin(), dateRange.getEnd());
            }
        */
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody QueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<SysReportTable> q = buildQuery(param);

        Page<SysReportTable> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


    @HasPermission
    @PostMapping("preview")
    public AjaxResult preview(@RequestBody SysReportChart param) throws Exception {
        String sql = param.getSql();
        Assert.hasText(sql, "请填写sql");

        ComplexResult data = sysReportSqlService.runSql(sql);
        return AjaxResult.ok().data(data);
    }



    @GetMapping("get")
    public AjaxResult get(String id) throws Exception {
        SysReportTable chart = service.findOne(id);

        return AjaxResult.ok().data(chart);
    }

    @Msg("查看报表")
    @HasPermission("sysChart:view")
    @GetMapping("getData/{code}")
    public AjaxResult draw(@PathVariable String code) throws Exception {
        SysReportTable table = service.findByCode(code);
        ComplexResult data = sysReportSqlService.runSql(table.getSql());
        service.addViewCount(code);
        return AjaxResult.ok().data(data);
    }

    @Msg("查看报表")
    @HasPermission("sysChart:view")
    @GetMapping("view/{code}")
    public void view(@PathVariable String code, HttpServletResponse response) throws Exception {
        SysReportTable table = service.findByCode(code);
        ComplexResult data = sysReportSqlService.runSql(table.getSql());

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
        response.getWriter().println(html.replace("OPTION", "xx"));
    }

}

