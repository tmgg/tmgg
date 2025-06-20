
package io.tmgg.modules.system.controller;

import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.entity.SysDict;
import io.tmgg.modules.system.service.SysDictService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sysDict")
public class SysDictController extends BaseController<SysDict> {

    @Resource
    private SysDictService sysDictService;




    @GetMapping("tree")
    @PublicRequest
    public AjaxResult tree() {
        return AjaxResult.ok().data(sysDictService.tree());
    }


}
