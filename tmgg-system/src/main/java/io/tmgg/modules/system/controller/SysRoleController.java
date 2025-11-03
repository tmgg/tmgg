package io.tmgg.modules.system.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.lang.tree.DictTreeNode;
import io.tmgg.lang.tree.TreeManager;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.system.dto.request.GrantMenuToRoleRequest;
import io.tmgg.modules.system.dto.request.GrantUserToRoleRequest;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.modules.system.service.SysMenuService;
import io.tmgg.modules.system.service.SysRoleService;
import io.tmgg.modules.system.service.SysUserService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.data.domain.BaseEntity;
import io.tmgg.web.pojo.param.DropdownParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统角色
 */
@RestController
@RequestMapping("sysRole")
public class SysRoleController extends BaseController<SysRole> {

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysHttpSessionService sm;

    @Resource
    private SysUserService sysUserService;


    /**
     * 添加系统角色
     */
    @Override
    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysRole role, RequestBodyKeys updateFields) throws Exception {
        role.setBuiltin(false);

        role = sysRoleService.saveOrUpdateByClient(role, updateFields);

        AjaxResult result = AjaxResult.ok().data(role).msg("保存角色成功");
        return result;
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

    @HasPermission("sysRole:save")
    @RequestMapping("ownMenu")
    public AjaxResult ownMenu(String id) {
        List<SysMenu> menuList = sysRoleService.ownMenu(id);
        List<String> leafIdList = TreeManager.of(menuList).getLeafIdList();

        return AjaxResult.ok().data("checked",leafIdList);
    }


    /**
     * 权限树 （菜单）
     *
     * @return
     */
    @HasPermission("sysRole:save")
    @RequestMapping("menuTree")
    public AjaxResult menuTree() {
        List<SysMenu> menus = sysMenuService.findAllValid();


        List<DictTreeNode> treeList = new ArrayList<>();
        for (SysMenu o : menus) {
            DictTreeNode d = new DictTreeNode();
            d.set("title", o.getName() );
            d.set("key", o.getId());
            d.set("parentKey", o.getPid());
            d.set("perm",StrUtil.nullToEmpty(o.getPerm()));

            d.set("id", o.getId());
            d.set("pid", o.getPid());

            treeList.add(d);
        }
        TreeManager<DictTreeNode> tm = TreeManager.of(treeList);

        return AjaxResult.ok().data(tm.getTree());
    }

    @HasPermission("sysRole:save")
    @RequestMapping("grantMenu")
    public AjaxResult grantMenu(@RequestBody GrantMenuToRoleRequest request) {
        sysRoleService.grantMenu(request.getId(), request.getMenuIds());
        return AjaxResult.ok().msg("授权菜单成功");
    }



    @HasPermission("sysRole:save")
    @RequestMapping("userList")
    public AjaxResult userList(String id) {
        List<SysUser> users = sysUserService.findAll();
        List<Dict> list = users.stream().map(u -> Dict.of("key", u.getId(), "title", u.getName())).toList();

        List<SysUser> ownUser = sysRoleService.findUsers(id);
        List<String> ownList = ownUser.stream().map(BaseEntity::getId).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("selectedKeys", ownList);

        return AjaxResult.ok().data(data);
    }




    @HasPermission("sysRole:save")
    @RequestMapping("grantUsers")
    public AjaxResult saveUserList(@RequestBody GrantUserToRoleRequest request) {
        sysRoleService.grantUsers(request.getId(), request.getUserIdList());
        return AjaxResult.ok().msg("授权用户成功");
    }

    @RequestMapping("options")
    public AjaxResult options(DropdownParam param) {
        String searchText = param.getSearchText();
        List<SysRole> list = sysRoleService.findValid();
        if (searchText != null) {
            list = list.stream().filter(t -> t.getName().contains(searchText)).toList();
        }

        List<Option> options = Option.convertList(list, BaseEntity::getId, SysRole::getName);

        return AjaxResult.ok().data(options);
    }

}

