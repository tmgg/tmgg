
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
public class SysDictDataController {

    @Resource
    private SysDictDataService sysDictDataService;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page(SysDictItem SysDictItem, Pageable pageable) {
        return AjaxResult.ok().data(sysDictDataService.page(SysDictItem, pageable));
    }


    @HasPermission
    @GetMapping("list")
    public AjaxResult list(SysDictItem SysDictItem) {
        return AjaxResult.ok().data(sysDictDataService.list(SysDictItem));
    }

    /**
     * 查看系统字典值
     *
     */
    @HasPermission
    @GetMapping("detail")
    public AjaxResult detail( SysDictItem SysDictItem) {
        return AjaxResult.ok().data(sysDictDataService.findOne(SysDictItem.getId()));
    }

    /**
     * 添加系统字典值
     *

 *
     */
    @HasPermission
    @PostMapping("add")
    public AjaxResult add(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.add(SysDictItem);
        return AjaxResult.ok();
    }

    /**
     * 删除系统字典值
     *

 *
     */
    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.delete(SysDictItem);
        return AjaxResult.ok();
    }

    /**
     * 编辑系统字典值
     *

 *
     */
    @HasPermission
    @PostMapping("edit")
    public AjaxResult edit(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.edit(SysDictItem);
        return AjaxResult.ok();
    }

    /**
     * 修改状态
     *
     *
 *
     */
    @HasPermission
    @PostMapping("changeStatus")
    public AjaxResult changeStatus(@RequestBody SysDictItem SysDictItem) {
        sysDictDataService.changeStatus(SysDictItem);
        return AjaxResult.ok();
    }

}
