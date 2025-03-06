package io.tmgg.weixin.controller;

import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.weixin.entity.WeixinMini;
import io.tmgg.weixin.service.WeixinService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weixinMini")
public class WexinMiniController extends BaseCURDController<WeixinMini> {

    @Resource
    private WeixinService service;




}
