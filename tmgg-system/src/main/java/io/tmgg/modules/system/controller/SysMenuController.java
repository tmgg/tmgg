package io.tmgg.modules.system.controller;


import io.tmgg.lang.TreeTool;
import io.tmgg.data.query.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeNode;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.service.SysMenuService;
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
    public AjaxResult page(@RequestParam(defaultValue = "false") Boolean showBtn, @RequestParam(defaultValue = "false") Boolean showFramework) throws IOException, ClassNotFoundException {
        JpaQuery<SysMenu> q = new JpaQuery<>();
        if (!showBtn) {
            q.ne(SysMenu.Fields.type, MenuType.BTN);
        }


        List<SysMenu> list = sysMenuService.findAll(q, Sort.by("seq"));
        List<SysMenu> sysMenus = TreeTool.buildTree(list);

        if (!showFramework) {
            sysMenus.removeIf(t -> t.getId().equals("sys"));
        }

        return AjaxResult.ok().data(sysMenus);
    }



    @GetMapping("menuTree")
    public AjaxResult menuTree() {
        List<TreeNode> data = sysMenuService.menuTree();
        return AjaxResult.ok().data(data);
    }


    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        sysMenuService.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }

}
