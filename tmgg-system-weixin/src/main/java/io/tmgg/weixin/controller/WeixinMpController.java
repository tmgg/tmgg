package io.tmgg.weixin.controller;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.weixin.entity.WeixinMp;
import io.tmgg.weixin.service.WeixinMpService;
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
import java.util.List;
import java.io.IOException;
@RestController
@RequestMapping("weixinMp")
public class WeixinMpController  extends BaseController<WeixinMp>{

    @Resource
    WeixinMpService service;


    // 查询参数，如果比较简单，可以直接用实体代替
    @Data
    public static class QueryParam {
        private  String keyword; // 仅有一个搜索框时的搜索文本


            private java.lang.String name;

            private java.lang.String appId;

            private java.lang.String appSecret;

            private java.lang.String remark;

            private java.lang.String token;

            private java.lang.String encodingAESKey;

    }

    private  JpaQuery<WeixinMp> buildQuery(QueryParam param) {
        JpaQuery<WeixinMp> q = new JpaQuery<>();

        // 一个关键字模糊搜索
        //  q.searchText(param.getKeyword(), 字段...);

        // 多个字段
        //  q.likeExample(param.getKeyword(), 字段...);


        // 单独判断，如时间区间
        /*  DateRange dateRange = param.getDateRange();
        if(dateRange != null && dateRange.isNotEmpty()){
            q.between(PointsItemRecord.Fields.redeemTime, dateRange.getBegin(), dateRange.getEnd());
        }*/

        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  QueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<WeixinMp> q = buildQuery(param);

        Page<WeixinMp> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


        @HasPermission
        @GetMapping({"exportExcel"})
        public void exportExcel(@RequestBody QueryParam param, HttpServletResponse resp) throws IOException {
            JpaQuery<WeixinMp> q = buildQuery(param);

            // 使用Excel注解
            this.service.exportExcel(q, "WeixinMp.xlsx",resp);

            /** 自定义列
            LinkedHashMap<String, Function<PointsRecord, Object>> columns = new LinkedHashMap<>();
            columns.put("姓名", t -> t.getPointsAccount().getRealName());
            columns.put("手机", t -> t.getPointsAccount().getPhone());
            columns.put("积分方向", t -> t.getDirection() == 1 ? "增加" : "减少");
            columns.put("获得积分", t -> t.getPoints());
            columns.put("时间", t -> DateUtil.formatDateTime(t.getTime()));
            columns.put("原因", t -> t.getReason());
             this.service.exportExcel(q, "WeixinMp.xlsx", columns,resp);
             **/
        }

}

