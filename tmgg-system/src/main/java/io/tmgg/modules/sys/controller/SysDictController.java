
package io.tmgg.modules.sys.controller;

import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysDict;
import io.tmgg.modules.sys.service.SysDictService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
