package io.tmgg.sys.watermask.controller;

import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.watermask.entity.Watermark;
import io.tmgg.sys.watermask.service.WatermarkService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("watermark")
public class WatermarkController extends BaseCURDController<Watermark> {

    @Resource
    private WatermarkService watermarkService;



}
