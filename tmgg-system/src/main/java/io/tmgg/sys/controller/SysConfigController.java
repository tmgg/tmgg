
package io.tmgg.sys.controller;


import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.sys.service.SysConfigService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.entity.SysConfig;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;


/**
 * 参数配置
 */
@RestController
@RequestMapping("sysConfig")
public class SysConfigController extends BaseCURDController<SysConfig> {

  @Resource
  private SysConfigService sysConfigService;

  @Override
  public AjaxResult save(SysConfig param) throws Exception {

    return super.save(param);
  }

  @Override
  public AjaxResult delete(String id) {
    Assert.state(false,"禁止删除");

    return super.delete(id);
  }
}


