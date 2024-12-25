
package io.tmgg.sys.controller;


import cn.hutool.core.util.StrUtil;
import io.tmgg.data.domain.PageExt;
import io.tmgg.lang.dao.specification.JpaQuery;
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


/**
 * 参数配置
 */
@RestController
@RequestMapping("sysConfig")
public class SysConfigController  {

  @Resource
  private SysConfigService service;


  @HasPermission
  @GetMapping("page")
  public AjaxResult page(String keyword, @PageableDefault(direction = Sort.Direction.DESC, sort = "id") Pageable pageable) {
    JpaQuery<SysConfig> q= new JpaQuery<>();
    if(StrUtil.isNotEmpty(keyword)){
      q.eq(SysConfig.Fields.label, keyword);
    }
    Page<SysConfig> page = service.findAll(q, pageable);

    // 总结栏示例
    PageExt<SysConfig> pageExt = new PageExt<>(page);
    pageExt.setTotalInfo("合计：" + page.getTotalElements());


    return AjaxResult.ok().data(pageExt);
  }

  @HasPermission
  @PostMapping("save")
  public AjaxResult save(@RequestBody SysConfig param) throws Exception {
    Assert.state(!param.isNew(), "仅限修改");
    SysConfig result = service.saveOrUpdate(param);
    return AjaxResult.ok().data( result.getId()).msg("保存成功");
  }

}


