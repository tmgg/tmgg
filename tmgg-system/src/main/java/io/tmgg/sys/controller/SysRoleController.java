package io.tmgg.sys.controller;

import io.tmgg.lang.TreeManager;
import io.tmgg.sys.perm.SysPerm;
import io.tmgg.sys.perm.SysPermService;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.service.SysUserService;
import io.tmgg.web.annotion.BusinessLog;
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
@BusinessLog("角色")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysPermService sysPermService;

    @Resource
    private SysUserService sysUserService;


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
    @BusinessLog("增改")
    public AjaxResult add(@RequestBody SysRole role) throws Exception {
        role.setBuiltin(false);
        role.setStatus(CommonStatus.ENABLE);


        sysRoleService.saveOrUpdate(role);

        return AjaxResult.ok();
    }


    @HasPermission(title = "删除")
    @GetMapping("delete")
    public AjaxResult delete(@RequestParam String id) {
        sysRoleService.deleteById(id);
        return AjaxResult.ok();
    }




    @HasPermission(title = "权限授权")
    @PostMapping("grantPerm")
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


    @HasPermission(title = "拥有菜单")
    @GetMapping("ownMenu")
    public AjaxResult ownMenu(String id) {
        List<String> menuIdList = sysRoleService.ownMenu(id);


        // 为了防止父子联动， 比如选择了某菜单，但并没有全选所有子节点，前段会默认勾选所有
        // 这里只返回叶子节点
        List<SysPerm> all = sysPermService.findAll();
        TreeManager<SysPerm> tm = TreeManager.newInstance(all);
        List<String> allLeafList = tm.getLeafIdList();


        List<String> leafList = menuIdList.stream().filter(allLeafList::contains).collect(Collectors.toList());
        return AjaxResult.ok().data(leafList);
    }


    @HasPermission("sysRole:page")
    @GetMapping("getUserIdsByRoleId")
    public AjaxResult getUserByRole(String roleId) {
        List<SysUser> users = sysUserService.findByRoleId(roleId);

        return AjaxResult.ok().data(users.stream().map(BaseEntity::getId).collect(Collectors.toList()));
    }


}

