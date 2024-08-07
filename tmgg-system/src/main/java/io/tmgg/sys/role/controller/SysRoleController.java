
package io.tmgg.sys.role.controller;

import io.minio.messages.Grant;
import io.tmgg.lang.TreeManager;
import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.sys.menu.service.SysMenuService;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.sys.user.service.SysUserService;
import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.param.SysRoleParam;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.validation.group.Detail;
import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    private SysMenuService sysMenuService;

    @Resource
    private SysUserService sysUserService;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page() {
        List<SysRole> list = sysRoleService.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        return AjaxResult.success(list);
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<SysRole> list = sysRoleService.findValid();

        List<Option> options = Option.convertList(list, BaseEntity::getId, SysRole::getName);

        return AjaxResult.success(options);
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

        return AjaxResult.success();
    }


    /**
     * 删除系统角色
     */
    @HasPermission
    @PostMapping("delete")
    @BusinessLog("删除")
    public AjaxResult delete(@RequestBody SysRoleParam sysRoleParam) {
        sysRoleService.delete(sysRoleParam);
        return AjaxResult.success();
    }


    /**
     * 查看系统角色
     */
    @HasPermission
    @GetMapping("detail")
    public AjaxResult detail(@Validated(Detail.class) SysRoleParam sysRoleParam) {
        return AjaxResult.success(sysRoleService.detail(sysRoleParam));
    }

    /**
     * 授权菜单
     */
    @HasPermission
    @PostMapping("grantMenu")
    @BusinessLog("授权菜单")
    public AjaxResult grantMenu(@RequestBody @Validated(Grant.class) SysRoleParam param) {
        List<String> grantMenuIdList = param.getGrantMenuIdList();

        // 选择子节点，同时也选中父节点
        Map<String, SysMenu> map = sysMenuService.findMap();
        Set<String> allMenuIds = map.keySet();
        // 过滤掉appid
        grantMenuIdList = grantMenuIdList.stream().filter(menuId -> allMenuIds.contains(menuId)).collect(Collectors.toList());

        Set<String> total = new HashSet<>();
        for (String menuId : grantMenuIdList) {
            SysMenu menu = map.get(menuId);
            total.add(menuId);


            while (map.containsKey(menu.getPid())) {
                total.add(menu.getPid());
                menu = map.get(menu.getPid());
            }
        }


        String roleId = param.getId();
        sysRoleService.grantMenu(roleId, total);

        // 立即刷新登录用户
        SysRole role = sysRoleService.findOne(roleId);
        for (SysUser user : role.getUsers()) {
           SecurityUtils.refresh(user.getId());
        }



        return AjaxResult.success("授权成功");
    }


    /**
     * 拥有菜单
     *

 *
     */
    @HasPermission
    @GetMapping("ownMenu")
    @BusinessLog("拥有菜单")
    public AjaxResult ownMenu(String id) {
        Set<String> menuIdList = sysRoleService.ownMenu(id);


        // 为了防止父子联动， 比如选择了某菜单，但并没有全选所有子节点，前段会默认勾选所有
        // 这里只返回叶子节点
        List<SysMenu> all = sysMenuService.findAll();
        TreeManager<SysMenu> tm = TreeManager.newInstance(all);
        List<String> allLeafList = tm.getLeafIdList();


        List<String> leafList = menuIdList.stream().filter(allLeafList::contains).collect(Collectors.toList());
        return AjaxResult.success(leafList);
    }


    @HasPermission("sysRole:page")
    @GetMapping("getUserIdsByRoleId")
    public AjaxResult getUserByRole(String roleId) {
        List<SysUser> users = sysUserService.findByRoleId(roleId);

        return AjaxResult.success(users.stream().map(BaseEntity::getId).collect(Collectors.toList()));
    }


    @HasPermission("sysRole:updateValue")
    @PostMapping("grantToUser")
    @BusinessLog("授权菜单")
    public AjaxResult grantToUser(@RequestBody GrantToUserParam param) {
        Assert.hasText(param.getRoleId(), "请选择角色");
        List<String> userIds = param.getCheckedUserIds();

        List<SysUser> oldUsers = sysUserService.findByRoleId(param.getRoleId());
        List<SysUser> newUsers = sysUserService.findAllById(userIds);


        List<SysUser> add = newUsers.stream().filter(u -> !oldUsers.contains(u)).collect(Collectors.toList());
        List<SysUser> remove = oldUsers.stream().filter(u -> !newUsers.contains(u)).collect(Collectors.toList());

        sysRoleService.changeRoleUser(param.getRoleId(), add, remove);


        // 立即刷新登录用户
        for (SysUser oldUser : oldUsers) {
            SecurityUtils.refresh(oldUser.getId());
        }

        for (SysUser newUser : newUsers) {
            SecurityUtils.refresh(newUser.getId());
        }

        return AjaxResult.success("授权成功");
    }


}

@Data
class GrantToUserParam {
    String roleId;
    List<String> checkedUserIds;
}
