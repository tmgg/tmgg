package io.tmgg.modules.sys.controller;


import io.tmgg.lang.TreeTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeNode;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("sysMenu")
public class SysMenuController {

    @Resource
    SysMenuService sysMenuService;

    @HasPermission
    @RequestMapping("list")
    public AjaxResult page() throws IOException, ClassNotFoundException {
        List<SysMenu> list = sysMenuService.findAll(Sort.by("seq"));

        List<SysMenu> sysMenus = TreeTool.buildTree(list);


        return AjaxResult.ok().data(sysMenus);
    }

    @HasPermission
    @RequestMapping("reset")
    public AjaxResult reset() throws Exception {
        sysMenuService.reset();
        return AjaxResult.ok();
    }



    @GetMapping("menuTree")
    public AjaxResult menuTree() {
        List<TreeNode> data = sysMenuService.menuTree();
        return AjaxResult.ok().data(data);
    }
}
