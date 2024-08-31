package io.tmgg.sys.controller;

import io.minio.messages.Grant;
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
import io.tmgg.web.validation.group.Detail;
import lombok.Data;
import org.springframework.data.domain.Sort;
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
    public AjaxResult add(@RequestBody SysRoleParam sysRoleParam) {
        if (sysRoleParam.getId() == null) {
            sysRoleService.add(sysRoleParam);
        } else {
            sysRoleService.edit(sysRoleParam);
        }

        return AjaxResult.ok();
    }


    @HasPermission(title = "删除")
    @PostMapping("delete")
    public AjaxResult delete(@RequestBody SysRoleParam sysRoleParam) {
        sysRoleService.delete(sysRoleParam);
        return AjaxResult.ok();
    }



    @HasPermission
    @GetMapping("detail")
    public AjaxResult detail(@Validated(Detail.class) SysRoleParam sysRoleParam) {
        return AjaxResult.ok().data(sysRoleService.detail(sysRoleParam));
    }


    @HasPermission(title = "权限授权")
    @PostMapping("grantMenu")
    public AjaxResult grantMenu(String roleId,  @RequestParam List<String> grantMenuIdList) {
        // 选择子节点，同时也选中父节点
        Map<String, SysPerm> map = sysPermService.findMap();
        Set<String> allMenuIds = map.keySet();
        // 过滤掉appid
        grantMenuIdList = grantMenuIdList.stream().filter(menuId -> allMenuIds.contains(menuId)).collect(Collectors.toList());

        Set<String> total = new HashSet<>();
        for (String menuId : grantMenuIdList) {
            SysPerm menu = map.get(menuId);
            total.add(menuId);


            while (map.containsKey(menu.getPid())) {
                total.add(menu.getPid());
                menu = map.get(menu.getPid());
            }
        }


        sysRoleService.grantMenu(roleId, total);

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

