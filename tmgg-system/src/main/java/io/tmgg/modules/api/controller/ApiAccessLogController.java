package io.tmgg.modules.api.controller;

import io.tmgg.modules.api.entity.ApiAccessLog;
import io.tmgg.modules.api.service.ApiAccessLogService;
import io.tmgg.web.persistence.BaseController;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("apiAccessLog")
public class ApiAccessLogController  extends BaseController<ApiAccessLog>{

    @Resource
    ApiAccessLogService service;




}

