
package io.tmgg.sys.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.sys.service.SysDictItemService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("sysDictItem")
public class SysDictItemController  {

    @Resource
    private SysDictItemService service;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page(String sysDictId, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        JpaQuery<SysDictItem> q = new JpaQuery<>();
        if(StrUtil.isNotEmpty(sysDictId)){
            q.eq(SysDictItem.Fields.sysDict + ".id",  sysDictId);
        }

        Page<SysDictItem> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysDictItem param) throws Exception {
        SysDictItem result = service.saveOrUpdate(param);
        return AjaxResult.ok().data( result.getId()).msg("保存成功");
    }



    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


}
