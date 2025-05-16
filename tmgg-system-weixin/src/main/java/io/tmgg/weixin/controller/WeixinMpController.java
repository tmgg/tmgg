package io.tmgg.weixin.controller;

import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.weixin.entity.WeixinMp;
import io.tmgg.weixin.service.WeixinMpService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("weixinMp")
public class WeixinMpController  extends BaseController<WeixinMp>{

    @Resource
    WeixinMpService service;






}

