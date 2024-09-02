
package io.tmgg.sys.controller;


import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.sys.service.SysConfigService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.entity.SysConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;


/**
 * 参数配置
 */
@RestController
@RequestMapping("sysConfig")
public class SysConfigController  {

  @Resource
  private SysConfigService service;


  @HasPermission
  @PostMapping("page")
  public AjaxResult page(@RequestBody SysConfig param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
    Page<SysConfig> page = service.findByExampleLike(param, pageable);
    return AjaxResult.ok().data(page);
  }

  @HasPermission
  @PostMapping("save")
  public AjaxResult save(@RequestBody SysConfig param) throws Exception {
    Assert.state(!param.isNew(), "仅限修改");
    SysConfig result = service.saveOrUpdate(param);
    return AjaxResult.ok().data( result.getId()).msg("保存成功");
  }

}


