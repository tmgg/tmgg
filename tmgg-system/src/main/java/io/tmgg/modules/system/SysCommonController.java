
package io.tmgg.modules.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.tmgg.config.SysProp;
import io.tmgg.lang.tree.TreeManager;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.dto.mapper.MenuMapper;
import io.tmgg.modules.system.dto.response.MenuResponse;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.modules.system.service.*;
import io.tmgg.web.enums.MenuType;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class SysCommonController {

    @Resource
    SysRoleService roleService;

    @Resource
    SysMenuService sysMenuService;


    @Resource
    SysConfigService sysConfigService;

    @Resource
    SysMenuBadgeService sysMenuBadgeService;

    @Resource
    SysFileService sysFileService;

    @Resource
    SysProp sysProp;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private MenuMapper menuMapper;

    /**
     * 站点信息， 非登录情况下使用
     */
    @PublicRequest
    @GetMapping("site-info")
    public AjaxResult siteInfo() {
        Map<String, Object> siteInfo = sysConfigService.findSiteInfo();
        String fileId = (String) siteInfo.get("loginBackground");
        boolean fileExist = sysFileService.isFileExist(fileId);
        if (!fileExist) {
            siteInfo.remove("loginBackground");
        }

        String publicKey = sysConfigService.getStr(Configs.RSA_PUBLIC_KEY);
        Assert.notNull(publicKey, "服务未初始化密钥信息，无法登录");

        siteInfo.put("rsaPublicKey", publicKey);

        String title = sysProp.getTitle();
        if (StrUtil.isNotBlank(title)) {
            siteInfo.put("title", title.trim());
        }


        return AjaxResult.ok().data(siteInfo);
    }


    /**
     * 获取当前登录信息
     */
    @GetMapping("getLoginInfo")
    private AjaxResult getLoginInfo(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        log.info("获取登录信息 {}", subject.getName());

        Dict vo = new Dict();
        vo.put("id", subject.getId());
        vo.put("name", subject.getName());
        vo.put("orgName", subject.getUnitName());
        vo.put("deptName", subject.getDeptName());
        vo.put("permissions", subject.getPermissions());
        vo.put("account", subject.getAccount());


        Set<String> roleIds = subject.getRoles();
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<SysRole> roleList = roleService.findAllByCode(roleIds);
            if (roleList.size() != roleIds.size()) {
                session.invalidate();
                Assert.state(false, "用户角色已被修改，请重新登录");
            }
            Set<String> roleNameSet = roleList.stream().map(SysRole::getName).collect(Collectors.toSet());
            String roleNames = StringUtils.join(roleNameSet, ",");
            vo.put("roleNames", roleNames);

        }

        return AjaxResult.ok().data(vo);
    }


    /**
     * 前端左侧菜单调用， 以展示顶部及左侧菜单
     */
    @GetMapping("menuInfo")
    public AjaxResult menuInfo() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("用户 {} 获取菜单信息, 权限码： {}", subject.getName(), subject.getPermissions());

        SysUser user = sysUserService.findOne(subject.getId());
        Set<SysRole> roles = user.getRoles();

        List<SysMenu> menuList = roleService.ownMenu(roles);
        List<SysMenu> onlyMenuList = menuList.stream().filter(t -> t.getType() != MenuType.BTN).toList();

        List<MenuResponse> menuResponseList = menuMapper.menuToResponseList(onlyMenuList);


        TreeManager<MenuResponse> tm = TreeManager.of(menuResponseList);
        List<MenuResponse> tree = tm.getTree();

        tree.removeIf(t-> CollUtil.isEmpty(t.getChildren()));

        Map<String, MenuResponse> treeMap = tm.getMap();
        tm.traverseTree(tree, item -> {
            if (item.getPid() == null) {
                item.setRootid(item.getId());
            } else {
                MenuResponse parent = treeMap.get(item.getPid());
                if (parent != null) {
                    item.setRootid(parent.getRootid());
                }
            }
        });


        Dict data = new Dict();

        List<Dict> topMenus = tree.stream().map(r -> Dict.of("key", r.getKey(), "label", r.getLabel())).toList();
        data.put("topMenus", topMenus);
        data.put("menus", tree);
        data.put("badgeList", sysMenuBadgeService.findAll());

        return AjaxResult.ok().data(data);
    }

}
