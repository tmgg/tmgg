package io.tmgg.weapp.controller.admin;

import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.weapp.entity.WeixinMiniapp;
import io.tmgg.weapp.service.WeappService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weapp")
public class WexinMiniappController extends BaseCURDController<WeixinMiniapp> {

    @Resource
    private WeappService service;




}
