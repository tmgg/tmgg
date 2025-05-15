package io.tmgg.weixin.controller;

import io.tmgg.web.persistence.BaseController;
import io.tmgg.weixin.entity.WeixinMini;
import io.tmgg.weixin.service.WeixinMiniService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weixinMini")
public class WexinMiniController extends BaseController<WeixinMini> {

    @Resource
    private WeixinMiniService service;




}
