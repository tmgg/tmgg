
package io.tmgg.sys.controller;

import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.sys.service.SysDictDataService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("sysDictItem")
public class SysDictItemController {

    @Resource
    private SysDictDataService sysDictDataService;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page(SysDictItem SysDictItem, Pageable pageable) {
        return AjaxResult.ok().data(sysDictDataService.page(SysDictItem, pageable));
    }


    @HasPermission
    @PostMapping("add")
    public AjaxResult add(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.add(SysDictItem);
        return AjaxResult.ok();
    }

    @HasPermission
    @PostMapping("edit")
    public AjaxResult edit(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.edit(SysDictItem);
        return AjaxResult.ok();
    }
    @HasPermission
    @PostMapping("changeStatus")
    public AjaxResult changeStatus(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.changeStatus(SysDictItem);
        return AjaxResult.ok();
    }

    /**
     * 删除系统字典值
     *
     */
    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.delete(SysDictItem);
        return AjaxResult.ok();
    }






}
