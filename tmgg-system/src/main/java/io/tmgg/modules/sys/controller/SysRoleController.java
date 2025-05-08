package io.tmgg.modules.sys.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.TreeNode;
import io.tmgg.lang.poi.ExcelImportTool;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.modules.sys.service.SysRoleService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统角色
 */
@RestController
@RequestMapping("sysRole")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysHttpSessionService sm;


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {


        JpaQuery<SysRole> q = new JpaQuery<>();
        q.searchText(param.getKeyword(),SysRole.Fields.name, SysRole.Fields.code);

        Page<SysRole> page = sysRoleService.findAll(q, pageable);
        return AjaxResult.ok().data(page).cls(SysRole.class);
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<SysRole> list = sysRoleService.findValid();

        List<Option> options = Option.convertList(list, BaseEntity::getId, SysRole::getName);

        return AjaxResult.ok().data(options);
    }

    /**
     * 添加系统角色
     */
    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysRole role) throws Exception {
        role.setBuiltin(false);

        List<SysMenu> newMenus = sysMenuService.findAllById(role.getMenuIds());
        List<String> perms = newMenus.stream().map(SysMenu::getPerm).filter(Objects::nonNull).toList();
        role.setPerms(perms);

        role= sysRoleService.saveOrUpdate(role);

        // 刷新 登录用户的权限
        List<Subject> list = sm.findAllSubject();
        for (Subject subject : list) {
            if(subject.hasRole(role.getCode())){
                sm.forceExistBySubjectId(subject.getId());
            }
        }

        AjaxResult result = AjaxResult.ok().data(role).msg("保存角色成功");
        return result;
    }


    @HasPermission
    @GetMapping("delete")
    public AjaxResult delete(@RequestParam String id) {
        sysRoleService.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }








    @RequestMapping("bizTree")
    public AjaxResult bizTree() {
        List<SysRole> list = sysRoleService.findValid();

        List<Dict> treeList = new ArrayList<>();
        for (SysRole sysOrg : list) {

            Dict d = new Dict();
            d.set("title", sysOrg.getName());
            d.set("key", sysOrg.getId());
            treeList.add(d);
        }

        return AjaxResult.ok().data(treeList);
    }

    /**
     * 权限树 （菜单）
     * @return
     */
    @HasPermission("sysRole:save")
    @RequestMapping("permTree")
    public AjaxResult permTree() {
        List<SysMenu> menus = sysMenuService.findAllValid();


        List<Dict> treeList = new ArrayList<>();
        for (SysMenu o : menus) {
            Dict d = new Dict();
            d.set("title", o.getName());
            d.set("key", o.getId());
            d.set("parentKey", o.getPid());



            treeList.add(d);
        }
        TreeManager<Dict> tm = TreeManager.of(treeList, "key", "parentKey");

        return AjaxResult.ok().data(tm.getTree());
    }

    @HasPermission("sysRole:save")
    @RequestMapping("ownMenu")
    public AjaxResult ownMenu(String id) {
        List<String> checked = sysRoleService.ownMenu(id);
        return AjaxResult.ok().data(checked);
    }


}

