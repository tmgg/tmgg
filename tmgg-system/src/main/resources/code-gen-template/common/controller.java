package ${modulePackageName}.controller;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
{modulePackageName}.entity.${name};
import ${modulePackageName}.service.${name}Service;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.CommonQueryParam;


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


}

