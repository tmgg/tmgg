package io.tmgg.weixin.controller;

import io.tmgg.web.persistence.BaseController;
import io.tmgg.weixin.entity.WeixinMp;
import io.tmgg.weixin.service.WeixinMpService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("weixinMp")
public class WeixinMpController  extends BaseController<WeixinMp>{

    @Resource
    WeixinMpService service;






}

