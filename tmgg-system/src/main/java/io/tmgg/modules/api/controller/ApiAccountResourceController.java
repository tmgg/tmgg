package io.tmgg.modules.api.controller;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.entity.ApiAccountResource;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import io.tmgg.web.persistence.BaseController;


import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

@RestController
@RequestMapping("apiAccountResource")
public class ApiAccountResourceController extends BaseController<ApiAccountResource>{

    @Resource
    ApiAccountResourceService service;





}

