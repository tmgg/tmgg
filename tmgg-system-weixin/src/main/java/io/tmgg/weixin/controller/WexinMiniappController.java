package io.tmgg.weixin.controller;

import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.weixin.entity.WeixinMiniapp;
import io.tmgg.weixin.service.WeappService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weapp")
public class WexinMiniappController extends BaseCURDController<WeixinMiniapp> {

    @Resource
    private WeappService service;




}
