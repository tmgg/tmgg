package ${modulePackageName}.controller;

import cn.crec.lang.obj.AjaxResult;
import cn.crec.lang.obj.Option;
import ${modulePackageName}.entity.${name};
import ${modulePackageName}.service.${name}Service;
import cn.crec.lang.dao.BaseCURDController;
import cn.crec.lang.dao.BaseEntity;
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

