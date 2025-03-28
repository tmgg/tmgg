package io.tmgg.modules.sys.controller;


import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeNode;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.MenuType;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("sysMenu")
public class SysMenuController {

    @Resource
    SysMenuService sysMenuService;

    @HasPermission
    @RequestMapping("list")
    public AjaxResult page(Boolean showBtn) throws IOException, ClassNotFoundException {
        List<SysMenu> list = sysMenuService.findAll(Sort.by("seq"));

        List<SysMenu> sysMenus = TreeTool.buildTree(list);


        return AjaxResult.ok().data(sysMenus);
    }



    @HasPermission(label = "修改图标")
    @RequestMapping("changeIcon")
    public AjaxResult changeIcon(@RequestBody SysMenu sysMenu) throws Exception {
        sysMenuService.changeIcon(sysMenu);
        return AjaxResult.ok();
    }

    @GetMapping("menuTree")
    public AjaxResult menuTree() {
        List<TreeNode> data = sysMenuService.menuTree();
        return AjaxResult.ok().data(data);
    }




    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(String id) {
        sysMenuService.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }

}
