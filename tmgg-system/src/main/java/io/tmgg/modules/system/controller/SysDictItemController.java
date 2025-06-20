
package io.tmgg.modules.system.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.system.entity.SysDict;
import io.tmgg.modules.system.entity.SysDictItem;
import io.tmgg.modules.system.service.SysDictItemService;
import io.tmgg.modules.system.service.SysDictService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("sysDictItem")
public class SysDictItemController  {

    @Resource
    private SysDictItemService service;

    @Resource
    private SysDictService sysDictService;




    @HasPermission(value = "sysDict:item-page",label = "查看明细")
    @RequestMapping("page")
    public AjaxResult page( String sysDictId, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        JpaQuery<SysDictItem> q = new JpaQuery<>();
        if(StrUtil.isNotEmpty(sysDictId)){
            q.eq(SysDictItem.Fields.sysDict + ".id",  sysDictId);
            Page<SysDictItem> page = service.findAll(q, pageable);
            return AjaxResult.ok().data(page);
        }else {
            return AjaxResult.ok().data(Page.empty(pageable));
        }
    }

    @HasPermission(value = "sysDict:item-save",label = "保存明细")
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysDictItem param, RequestBodyKeys updateFields) throws Exception {
        SysDict dict = sysDictService.findOne(param.getSysDict().getId());
        if(dict.getIsNumber()){
            String code = param.getCode();
            Assert.state(NumberUtil.isNumber(code), "编码非数字");
        }

        param.setBuiltin(false);
        SysDictItem result = service.saveOrUpdate(param,updateFields);
        return AjaxResult.ok().data( result.getId()).msg("保存成功");
    }



    @HasPermission(value = "sysDict:item-delete",label = "删除明细")
    @PostMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


}
