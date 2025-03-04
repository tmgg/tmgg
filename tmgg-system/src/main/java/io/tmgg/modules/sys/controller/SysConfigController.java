
package io.tmgg.modules.sys.controller;


import cn.hutool.core.util.StrUtil;
import io.tmgg.data.domain.PageExt;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysConfig;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;


/**
 * 参数配置
 */
@RestController
@RequestMapping("sysConfig")
public class SysConfigController  {

  @Resource
  private SysConfigService service;


  @HasPermission
  @RequestMapping("page")
  public AjaxResult page(@RequestBody CommonQueryParam queryParam,
                         @PageableDefault(sort = {"seq", "group"}) Pageable pageable) {
    JpaQuery<SysConfig> q= new JpaQuery<>();
    q.searchText(queryParam.getKeyword(), SysConfig.Fields.label, "id", SysConfig.Fields.remark);
    Page<SysConfig> page = service.findAll(q, pageable);

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


