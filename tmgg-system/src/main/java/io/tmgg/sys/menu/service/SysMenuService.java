
package io.tmgg.sys.menu.service;

import io.tmgg.core.event.LogoutEvent;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.obj.Route;
import io.tmgg.sys.app.dao.SysAppDao;
import io.tmgg.sys.app.entity.SysApp;
import io.tmgg.sys.menu.dao.SysMenuDao;
import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.sys.menu.node.MenuTreeNode;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.user.dao.SysUserDao;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.web.enums.AdminType;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统菜单service接口实现类
 */
@Service
@Slf4j
public class SysMenuService extends BaseService<SysMenu> {


    @Resource
    private SysMenuDao sysMenuDao;

    @Resource
    private SysAppDao sysAppDao;
    @Resource
    private SysUserDao sysUserDao;


    /**
     * 不含按钮 及 不显示的东西
     */
    public List<Route> findAllAppMenuList() {

        List<SysMenu> sysMenuList = sysMenuDao.findMenuVisible();

        List<Route> routes = new LinkedList<>();


        for (SysMenu m : sysMenuList) {
            String pid = m.getPid();
            // 根节点挂载到对于的app
            if (pid == null || pid.equals("") || pid.equals("0")) {
                pid = m.getApplication();
            }

            // iframe设置完整url
            String url = m.getRouter();

            Route route = new Route(String.valueOf(m.getId()), pid, m.getName(), url, null, m.getApplication());
            route.setIcon(m.getIcon());
            route.setPerm(StrUtil.emptyToNull(m.getPermission()));
            route.setIframe(m.getIframe());
            routes.add(route);
        }

        return routes;
    }

    public List<Route> getAppRoute() {
        List<Route> routes = new ArrayList<>();
        List<SysApp> apps = sysAppDao.findValid();
        for (SysApp app : apps) {
            String code = app.getCode();
            routes.add(new Route(code, null, app.getName(), null, null, code));


        }
        return routes;
    }


    public List<MenuTreeNode> treeForGrant() {
        List<SysMenu> all = sysMenuDao.findAllValid();
        List<SysApp> apps = sysAppDao.findValid();

        Collection<MenuTreeNode> nodes = new ArrayList<>();

        for (SysApp app : apps) {
            MenuTreeNode menuTreeNode = new MenuTreeNode();
            menuTreeNode.setId(app.getCode());
            menuTreeNode.setValue(app.getCode());
            menuTreeNode.setTitle(app.getName());
            menuTreeNode.setWeight(app.getSeq());
            nodes.add(menuTreeNode);
        }


        for (SysMenu sysMenu : all) {
            MenuTreeNode menuTreeNode = new MenuTreeNode();
            menuTreeNode.setId(sysMenu.getId());
            menuTreeNode.setPid(StrUtil.emptyToDefault(sysMenu.getPid(), sysMenu.getApplication()));
            menuTreeNode.setValue(sysMenu.getId());
            String titleAddon = StrUtil.isEmpty(sysMenu.getPermission()) ? "" : " [" + sysMenu.getPermission() + "]";
            menuTreeNode.setTitle(sysMenu.getName() + titleAddon);
            menuTreeNode.setWeight(sysMenu.getSeq());
            nodes.add(menuTreeNode);
        }

        List<MenuTreeNode> tree = TreeTool.buildTree(nodes);
        return tree;
    }


    @EventListener
    public void onLogout(LogoutEvent e) {
        log.info("退出系统事件 {}", e.getUserId());
    }


    public Map<String, SysMenu> findMap() {
        Map<String, SysMenu> map = new HashMap<>();
        List<SysMenu> all = this.findAll();
        for (SysMenu sysMenu : all) {
            map.put(sysMenu.getId(), sysMenu);
        }
        return map;
    }

    public Collection<String> getLoginPermissions(String userId) {
        SysUser user = sysUserDao.findOne(userId);

        Set<String> list = new HashSet<>();

        // 管理员则返回所有权限
        if (user.getAdminType() == AdminType.SUPER_ADMIN) {
            List<SysMenu> allValid = sysMenuDao.findAllValid();
            for (SysMenu m : allValid) {
                list.add(m.getPermission());
            }
        } else {
            Set<SysRole> roles = user.getRoles();
            for (SysRole role : roles) {
                if (role.getStatus() != CommonStatus.ENABLE) {
                    continue;
                }
                Set<SysMenu> roleMenus = role.getMenus();
                for (SysMenu menu : roleMenus) {
                    if (menu.getStatus() != CommonStatus.ENABLE) {
                        continue;
                    }
                    list.add(menu.getPermission());
                }
            }
        }

        return list.stream().filter(Objects::nonNull).sorted().collect(Collectors.toList());
    }
}
