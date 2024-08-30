
package io.tmgg.sys.controller;


import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.entity.SysConfig;
import io.tmgg.sys.app.service.SysConfigService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;


/**
 * 参数配置
 */
@RestController
@RequestMapping("sysConfig")
public class SysConfigController {

  @Resource
  private SysConfigService sysConfigService;


  @HasPermission
  @GetMapping("page")
  public AjaxResult page() {
    List<SysConfig> list = sysConfigService.findAll();
    return AjaxResult.ok().data(list);
  }



  @HasPermission
  @PostMapping("edit")
  @BusinessLog("系统参数配置_编辑")
  public AjaxResult edit(@RequestBody SysConfig SysConfig) {
    sysConfigService.updateValue(SysConfig);
    return AjaxResult.ok();
  }

}


