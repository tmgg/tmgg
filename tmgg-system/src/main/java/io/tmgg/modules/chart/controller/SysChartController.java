package io.tmgg.modules.chart.controller;

import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.chart.entity.SysChart;
import io.tmgg.modules.chart.service.SysChartService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import lombok.Data;
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

}

