
package io.tmgg.sys.dict.controller;

import io.tmgg.sys.dict.entity.SysDictData;
import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.dict.service.SysDictDataService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 系统字典值控制器
 *

 *
 */
@RestController
public class SysDictDataController {

    @Resource
    private SysDictDataService sysDictDataService;

    /**
     * 查询系统字典值
     *

 *
     */
    @HasPermission
    @GetMapping("/sysDictData/page")
    @BusinessLog("系统字典值_查询")
    public AjaxResult page(SysDictData SysDictData, Pageable pageable) {
        return AjaxResult.ok().data(sysDictDataService.page(SysDictData, pageable));
    }

    /**
     * 某个字典类型下所有的字典
     *

 *
     */
    @HasPermission
    @GetMapping("/sysDictData/list")
    @BusinessLog("系统字典值_列表")
    public AjaxResult list(SysDictData SysDictData) {
        return AjaxResult.ok().data(sysDictDataService.list(SysDictData));
    }

    /**
     * 查看系统字典值
     *

 *
     */
    @HasPermission
    @GetMapping("/sysDictData/detail")
    @BusinessLog("系统字典值_查看")
    public AjaxResult detail( SysDictData SysDictData) {
        return AjaxResult.ok().data(sysDictDataService.findOne(SysDictData.getId()));
    }

    /**
     * 添加系统字典值
     *

 *
     */
    @HasPermission
    @PostMapping("/sysDictData/add")
    @BusinessLog("系统字典值_增加")
    public AjaxResult add(@RequestBody SysDictData SysDictData) {
        sysDictDataService.add(SysDictData);
        return AjaxResult.ok();
    }

    /**
     * 删除系统字典值
     *

 *
     */
    @HasPermission
    @PostMapping("/sysDictData/delete")
    @BusinessLog("系统字典值_删除")
    public AjaxResult delete(@RequestBody SysDictData SysDictData) {
        sysDictDataService.delete(SysDictData);
        return AjaxResult.ok();
    }

    /**
     * 编辑系统字典值
     *

 *
     */
    @HasPermission
    @PostMapping("/sysDictData/edit")
    @BusinessLog("系统字典值_编辑")
    public AjaxResult edit(@RequestBody SysDictData SysDictData) {
        sysDictDataService.edit(SysDictData);
        return AjaxResult.ok();
    }

    /**
     * 修改状态
     *
     *
 *
     */
    @HasPermission
    @PostMapping("/sysDictData/changeStatus")
    @BusinessLog("系统字典值_修改状态")
    public AjaxResult changeStatus(@RequestBody SysDictData SysDictData) {
        sysDictDataService.changeStatus(SysDictData);
        return AjaxResult.ok();
    }

}
