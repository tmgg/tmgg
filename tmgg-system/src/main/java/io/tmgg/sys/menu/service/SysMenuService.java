
package io.tmgg.sys.menu.service;

import io.tmgg.core.event.LogoutEvent;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.obj.Route;
import io.tmgg.sys.menu.dao.SysMenuDao;
import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.sys.menu.node.MenuTreeNode;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.user.dao.SysUserDao;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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
    private SysUserDao sysUserDao;


    /**
     * 不含按钮 及 不显示的东西
     */
    public List<Route> findAllAppMenuList() {
        List<SysMenu> sysMenuList = sysMenuDao.findMenuVisible();

        List<Route> routes = new LinkedList<>();


        for (SysMenu m : sysMenuList) {
            String pid = m.getPid();

            // iframe设置完整url
            String url = m.getRouter();

            Route route = new Route(String.valueOf(m.getId()), pid, m.getName(), url, null);
            route.setIcon(m.getIcon());
            route.setPerm(StrUtil.emptyToNull(m.getPermission()));
            route.setIframe(m.getIframe());
            routes.add(route);
        }

        return routes;
    }




    public List<MenuTreeNode> treeForGrant() {
        List<SysMenu> all = sysMenuDao.findAllValid();

        Collection<MenuTreeNode> nodes = new ArrayList<>();




        for (SysMenu sysMenu : all) {
            MenuTreeNode menuTreeNode = new MenuTreeNode();
            menuTreeNode.setId(sysMenu.getId());
            menuTreeNode.setPid(sysMenu.getPid());
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


}
