
package io.tmgg.modules.sys.controller;

import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysDict;
import io.tmgg.modules.sys.service.SysDictService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("sysDict")
public class SysDictController extends BaseCURDController<SysDict> {

    @Resource
    private SysDictService sysDictService;




    @GetMapping("tree")
    @PublicApi
    public AjaxResult tree() {
        return AjaxResult.ok().data(sysDictService.tree());
    }



}
