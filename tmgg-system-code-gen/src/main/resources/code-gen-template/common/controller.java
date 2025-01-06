package ${modulePackageName}.controller;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import ${modulePackageName}.entity.${name};
import ${modulePackageName}.service.${name}Service;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseEntity;
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
public class ${name}Controller  {

    @Resource
    ${name}Service service;


    @Getter
    @Setter
    public static class QueryParam {
        private  String keyword; // 仅有一个搜索框时的搜索文本

    }

    @HasPermission
    @GetMapping("page")
    public AjaxResult page(QueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        ${name} example = new ${name}();
        BeanUtil.copyProperties(param, example);

        JpaQuery<${name}> q = new JpaQuery<>();
        q.likeExample(param);

        // 构造查询条件

        //  q.searchText(keyword, "name", "phone");

        Page<${name}> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody ${name} param) throws Exception {
        ${name} result = service.saveOrUpdate(param);
        return AjaxResult.ok().data( result.getId()).msg("保存成功");
    }



    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }




}

