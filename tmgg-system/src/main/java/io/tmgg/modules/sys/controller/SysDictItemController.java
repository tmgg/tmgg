
package io.tmgg.modules.sys.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.entity.SysDictItem;
import io.tmgg.modules.sys.service.SysDictItemService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import lombok.Data;
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


    @Data
    public static class QueryParam{
        String keyword;
        String sysDictId;
    }

    @Msg("字典项目列表")
    @HasPermission("sysDict:item-page")
    @RequestMapping("page")
    public AjaxResult page(@RequestBody QueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        String sysDictId = param.getSysDictId();
        JpaQuery<SysDictItem> q = new JpaQuery<>();
        if(StrUtil.isNotEmpty(sysDictId)){
            q.eq(SysDictItem.Fields.sysDict + ".id",  sysDictId);
        }

        Page<SysDictItem> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

    @Msg("字典项目保存")
    @HasPermission("sysDict:item-save")
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysDictItem param) throws Exception {
        SysDictItem result = service.saveOrUpdate(param);
        return AjaxResult.ok().data( result.getId()).msg("保存成功");
    }



    @Msg("字典项目删除")
    @HasPermission("sysDict:item-delete")
    @PostMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


}
