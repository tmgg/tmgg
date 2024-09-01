package io.tmgg.sys.controller;

import io.tmgg.lang.TreeManager;
import io.tmgg.lang.ann.Remark;
import io.tmgg.sys.perm.SysPerm;
import io.tmgg.sys.perm.SysPermService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.param.SysRoleParam;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.validation.group.Detail;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.*;
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
    private SysPermService sysPermService;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page() {
        List<SysRole> list = sysRoleService.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        return AjaxResult.ok().data(list);
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
        role.setStatus(CommonStatus.ENABLE);


        role= sysRoleService.saveOrUpdate(role);

        AjaxResult result = AjaxResult.ok().data(role).msg("保存角色成功");
        return result;
    }


    @HasPermission
    @GetMapping("delete")
    public AjaxResult delete(@RequestParam String id) {
        sysRoleService.deleteById(id);
        return AjaxResult.ok();
    }


    @HasPermission(value = "sysRole:grant")
    @GetMapping("ownMenu")
    @Remark("权限授权")
    public AjaxResult ownMenu(String id) {
        List<String> menuIdList = sysRoleService.ownMenu(id);
        List<SysPerm> all = sysPermService.findAll();
        TreeManager<SysPerm> tm = TreeManager.newInstance(all);
        List<String> allLeafList = tm.getLeafIdList();

        List<String> leafList = menuIdList.stream().filter(allLeafList::contains).collect(Collectors.toList());
        return AjaxResult.ok().data(leafList);
    }

    @HasPermission( "sysRole:grant")
    @PostMapping("grantPerm")
    @Remark("权限授权")
    public AjaxResult grantPerm(@RequestParam(required = true) String id, @RequestParam(required = false) List<String> permIds) {
        sysRoleService.grantPerm(id, permIds);


        // 刷新 登录用户的权限
        SysRole role = sysRoleService.findOne(id);
        List<Subject> list = SecurityUtils.findAll();
        for (Subject subject : list) {
            if(subject.hasRole(role.getCode())){
                SecurityUtils.refresh(subject.getId());
            }
        }

        return AjaxResult.ok().msg("授权成功");
    }






}

