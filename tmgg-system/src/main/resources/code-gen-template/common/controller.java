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
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("${firstLowerName}")
public class ${name}Controller  extends BaseController{

    @Resource
    ${name}Service service;


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

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  QueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        ${name} example = new ${name}();
        BeanUtil.copyProperties(param, example);

        JpaQuery<${name}> q = new JpaQuery<>();

        q.likeExample(example);

        // 构造查询条件

        //  q.searchText(keyword, "name", "phone");

        Page<${name}> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }






}

