package ${modulePackageName}.controller;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import ${modulePackageName}.entity.${name};
import ${modulePackageName}.service.${name}Service;
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
@RequestMapping("${firstLowerName}")
public class ${name}Controller  extends BaseController<${name}>{

    @Resource
    ${name}Service service;






    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  QueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<${name}> q = buildQuery(param);

        Page<${name}> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


        @HasPermission
        @GetMapping({"exportExcel"})
        public void exportExcel(@RequestBody QueryParam param, HttpServletResponse resp) throws IOException {
            JpaQuery<${name}> q = buildQuery(param);
            List<${name}> list = service.findAll(q);

            // 使用Excel注解
            this.service.exportExcel(list, "${name}.xlsx",resp);

            /** 自定义列 示例
             Table<${name}> tb = new Table<>(list);
             tb.addColumn("姓名", SysUser::getName);
             tb.addColumn("账号", SysUser::getAccount);
             tb.addColumn("手机号", SysUser::getPhone);


             ExcelExportTool.exportTable("用户列表.xlsx", tb,  response);
             **/
        }


        // 查询，导出公用条件构造
        private  JpaQuery<${name}> buildQuery(QueryParam param) {
            JpaQuery<${name}> q = new JpaQuery<>();

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

// 查询参数，如果比较简单，可以直接用实体代替
    @Data
    public static class QueryParam {
         private  String keyword; // 仅有一个搜索框时的搜索文本

        <#list queryFields as f>

         <#if (f.type == 'java.util.Date')>
             private DateRange ${f.name}Range;
         <#else>
        private ${f.type} ${f.name};
         </#if>
        </#list>

}
}

