
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

@RestController
@RequestMapping("sysDict")
public class SysDictController extends BaseController<SysDict> {

    @Resource
    private SysDictService sysDictService;


    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(@RequestBody SysDict param, String keyword, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<SysDict> q = new JpaQuery<>();
        q.likeExample(param);

        q.searchText(keyword, "name", "code");

        Page<SysDict> page = sysDictService.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


    @HasPermission
    @PostMapping("save")
    @Override
    public AjaxResult save(@RequestBody SysDict param) throws Exception {
        sysDictService.save(param);
        return AjaxResult.ok();
    }

    @GetMapping("tree")
    @PublicRequest
    public AjaxResult tree() {
        return AjaxResult.ok().data(sysDictService.tree());
    }


}
