package ${modulePackageName}.controller;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import ${modulePackageName}.entity.${name};
import ${modulePackageName}.service.${name}Service;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("${firstLowerName}")
public class ${name}Controller extends BaseCURDController<${name}> {

    @Resource
    ${name}Service service;

    @HasPermission
    @GetMapping("page")
    public AjaxResult page(${name} param, String keyword, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<${name}> q = new JpaQuery<>();
        q.likeExample(param);

        //  q.searchText(keyword, "name", "phone");

        Page<${name}> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

}

