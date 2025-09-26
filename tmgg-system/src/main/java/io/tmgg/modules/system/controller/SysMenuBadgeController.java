package io.tmgg.modules.system.controller;

import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.entity.SysMenuBadge;
import io.tmgg.modules.system.service.SysMenuBadgeService;
import io.tmgg.modules.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sysMenuBadge")
public class SysMenuBadgeController  extends BaseController<SysMenuBadge>{

    @Resource
    SysMenuBadgeService service;

    @Resource
    SysMenuService sysMenuService;




    @GetMapping("menuOptions")
    public AjaxResult menuOptions() throws Exception {
        List<SysMenu> menus = sysMenuService.findMenuVisible();

        List<TreeOption> treeOptions = TreeOption.convertTree(menus, BaseEntity::getId, SysMenu::getPid, SysMenu::getName);

        return AjaxResult.ok().data(treeOptions);
    }
}

