package io.tmgg.modules.sys.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        return AjaxResult.ok().data(page);
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


        role= sysRoleService.saveOrUpdate(role);

        AjaxResult result = AjaxResult.ok().data(role).msg("保存角色成功");
        return result;
    }


    @HasPermission
    @GetMapping("delete")
    public AjaxResult delete(@RequestParam String id) {
        sysRoleService.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


    @HasPermission(value = "sysRole:grant")
    @GetMapping("ownMenu")
    @Msg("权限授权")
    public AjaxResult ownMenu(String id) {
        List<String> menuIdList = sysRoleService.ownMenu(id);
        List<SysMenu> all = sysMenuService.findAll();
        TreeManager<SysMenu> tm = TreeManager.of(all);
        List<String> allLeafList = tm.getLeafIdList();

        List<String> leafList = menuIdList.stream().filter(allLeafList::contains).collect(Collectors.toList());
        return AjaxResult.ok().data(leafList);
    }

    @HasPermission( "sysRole:grant")
    @PostMapping("grantPerm")
    @Msg("权限授权")
    public AjaxResult grantPerm(@RequestParam(required = true) String id, @RequestParam(required = false) List<String> permIds) {
        sysRoleService.grantPerm(id, permIds);

        // 刷新 登录用户的权限
        SysRole role = sysRoleService.findOne(id);
        List<Subject> list = sm.findAllSubject();
        for (Subject subject : list) {
            if(subject.hasRole(role.getCode())){
                sm.forceExistBySubjectId(subject.getId());
            }
        }

        return AjaxResult.ok().msg("权限授权成功");
    }



    @HasPermission
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


}

