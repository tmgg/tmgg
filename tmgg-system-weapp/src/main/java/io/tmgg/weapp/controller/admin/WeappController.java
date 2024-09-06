package io.tmgg.weapp.controller.admin;

import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.weapp.entity.Weapp;
import io.tmgg.weapp.service.WeappService;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weapp")
public class WeappController extends BaseCURDController<Weapp> {

    @Resource
    private WeappService service;




}
