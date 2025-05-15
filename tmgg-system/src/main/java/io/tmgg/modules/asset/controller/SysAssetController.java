package io.tmgg.modules.asset.controller;

import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.asset.entity.SysAsset;
import io.tmgg.modules.asset.service.SysAssetService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.persistence.BaseController;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sysAsset")
public class SysAssetController  extends BaseController<SysAsset>{

    @Resource
    SysAssetService service;




    @HasPermission(label = "编辑内容")
    @PostMapping("saveContent")
    public AjaxResult saveContent(@RequestBody SysAsset param) throws Exception {
         service.saveContent(param);
        return AjaxResult.ok().msg("保存成功");
    }



    @PublicRequest
    @GetMapping({"preview/{name}" ,"preview/{name}.*" }) // 可以加后缀，如 /sysAsset/preview/bg.png
    public void preview(@PathVariable String name, HttpServletResponse resp) throws Exception {
        service.preview(name, resp);
    }
}

