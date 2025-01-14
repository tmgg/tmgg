package io.tmgg.modules.chart.controller;

import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.chart.entity.SysChart;
import io.tmgg.modules.chart.service.SysChartService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("sysChart")
public class SysChartController extends BaseController<SysChart> {

    @Resource
    SysChartService service;


    @Data
    public static class QueryParam {
        private String keyword; // 仅有一个搜索框时的搜索文本


        private java.lang.String name;

        private java.lang.String sqlContent;

        private java.lang.String category;

    }

    private JpaQuery<SysChart> buildQuery(QueryParam param) {
        JpaQuery<SysChart> q = new JpaQuery<>();
      /*  DateRange dateRange = param.getDateRange();
        if(dateRange != null && dateRange.isNotEmpty()){
            q.between(PointsItemRecord.Fields.redeemTime, dateRange.getBegin(), dateRange.getEnd());
        }*/
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody QueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<SysChart> q = buildQuery(param);

        Page<SysChart> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }



    @HasPermission
    @PostMapping("preview")
    public AjaxResult preview(@RequestBody SysChart param) throws Exception {
        String sql = param.getSql();
        Assert.hasText(sql,"请填写sql");
        SysChart chart = service.findOne(param.getId());
        Map<String, Object> option = service.buildEchartsOption(chart.getTitle(), param.getType(),  service.runSql(sql));

        return AjaxResult.ok().data(option);
    }

    @GetMapping("get")
    public AjaxResult get(String id) throws Exception {
        SysChart chart = service.findOne(id);

        return AjaxResult.ok().data(chart);
    }

    @HasPermission
    @GetMapping("getOption/{code}")
    public AjaxResult draw(@PathVariable String code) throws Exception {
        SysChart chart = service.findByCode(code);
        Map<String, Object> option = service.buildEchartsOption(chart.getTitle(), chart.getType(),  service.runSql(chart.getSql()));

        return AjaxResult.ok().data(option);
    }

    @HasPermission
    @GetMapping("view/{code}")
    public void view(@PathVariable String code, HttpServletResponse response) throws Exception {
        SysChart chart = service.findByCode(code);
        Map<String, Object> option = service.buildEchartsOption(chart.getTitle(), chart.getType(),  service.runSql(chart.getSql()));
        String json = JsonTool.toPrettyJsonQuietly(option);

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

