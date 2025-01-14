package io.tmgg.modules.chart.controller;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.chart.entity.SysChart;
import io.tmgg.modules.chart.service.SysChartService;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.DateRange;


import io.tmgg.web.annotion.HasPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("sysChart")
public class SysChartController  extends BaseController<SysChart>{

    @Resource
    SysChartService service;


    @Data
    public static class QueryParam {
        private  String keyword; // 仅有一个搜索框时的搜索文本


            private java.lang.String name;

            private java.lang.String sqlContent;

            private java.lang.String category;

    }

    private  JpaQuery<SysChart> buildQuery(QueryParam param) {
        JpaQuery<SysChart> q = new JpaQuery<>();
      /*  DateRange dateRange = param.getDateRange();
        if(dateRange != null && dateRange.isNotEmpty()){
            q.between(PointsItemRecord.Fields.redeemTime, dateRange.getBegin(), dateRange.getEnd());
        }*/
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  QueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<SysChart> q = buildQuery(param);

        Page<SysChart> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


        @HasPermission
        @GetMapping({"exportExcel"})
        public void exportExcel(@RequestBody QueryParam param, HttpServletResponse resp) throws IOException {
            JpaQuery<SysChart> q = buildQuery(param);

            // 使用Excel注解
            this.service.exportExcel(q, "SysChart.xlsx",resp);

            /** 自定义列
            LinkedHashMap<String, Function<PointsRecord, Object>> columns = new LinkedHashMap<>();
            columns.put("姓名", t -> t.getPointsAccount().getRealName());
            columns.put("手机", t -> t.getPointsAccount().getPhone());
            columns.put("积分方向", t -> t.getDirection() == 1 ? "增加" : "减少");
            columns.put("获得积分", t -> t.getPoints());
            columns.put("时间", t -> DateUtil.formatDateTime(t.getTime()));
            columns.put("原因", t -> t.getReason());
             this.service.exportExcel(q, "SysChart.xlsx", columns,resp);
             **/
        }

}

