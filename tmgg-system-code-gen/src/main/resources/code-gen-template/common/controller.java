package ${modulePackageName}.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import ${modulePackageName}.entity.${name};
import ${modulePackageName}.service.${name}Service;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseEntity;
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



}

