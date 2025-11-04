
package io.tmgg.modules.system.service;

import io.tmgg.common.AntDesignIcon;
import io.tmgg.lang.tree.TreeManager;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.obj.TreeNode;
import io.tmgg.modules.system.dao.JsonEntityFileDao;
import io.tmgg.modules.system.dao.SysMenuDao;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.web.enums.MenuType;
import io.tmgg.data.domain.BaseEntity;
import io.tmgg.data.service.BaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    JsonEntityFileDao jsonEntityFileDao;


    /**
     * 不含按钮 及 不显示的东西
     */
    public Map<String, SysMenu> findMenuMap() {
        List<SysMenu> sysMenuList = sysMenuDao.findMenuVisible();
        return sysMenuList.stream().collect(Collectors.toMap(BaseEntity::getId, t -> t));
    }


    public List<SysMenu> findAllValid() {
        return sysMenuDao.findAllValid();
    }

    public List<SysMenu> findMenuVisible() {
        return sysMenuDao.findMenuVisible();
    }

    public List<TreeNode> menuTree() {
        List<SysMenu> all = sysMenuDao.findMenuVisible();

        Collection<TreeNode> nodes = new ArrayList<>();

        return convertTreeNode(all, nodes);
    }


    public List<TreeNode> convertTreeNode(List<SysMenu> all, Collection<TreeNode> nodes) {
        for (SysMenu sysMenu : all) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(sysMenu.getId());
            treeNode.setPid(sysMenu.getPid());
            treeNode.setValue(sysMenu.getId());
            treeNode.setTitle(sysMenu.getName());
            treeNode.setWeight(sysMenu.getSeq());
            nodes.add(treeNode);
        }

        return TreeTool.buildTree(nodes);
    }





    public List<String> findPidList(List<String> menuIds) {
        List<SysMenu> list = sysMenuDao.findAll();

        TreeManager<SysMenu> tm = TreeManager.of(list);

        Set<String> ids = new HashSet<>();
        for (String menuId : menuIds) {
            ids.addAll(tm.getParentIdListById(menuId));
        }

        return new ArrayList<>(ids);
    }


    public void addMenu(String pid, String id, String name, AntDesignIcon icon, String perm, String path, int seq, boolean refreshOnTabClick) {
        SysMenu menu = sysMenuDao.findOne(id);
        if (menu == null) {
            menu = new SysMenu();
        }

        menu.setId(id);
        menu.setPid(pid);
        menu.setName(name);
        menu.setPerm(perm);
        menu.setPath(path);
        menu.setRefreshOnTabClick(refreshOnTabClick);
        menu.setSeq(seq);
        if (icon != null) {
            menu.setIcon(icon.name());

        }

        sysMenuDao.save(menu);
    }

    public void addDir(String pid, String id, String name, AntDesignIcon icon, int seq) {
        SysMenu menu = sysMenuDao.findOne(id);
        if (menu == null) {
            menu = new SysMenu();
        }

        menu.setId(id);
        menu.setPid(pid);
        menu.setName(name);
        menu.setType(MenuType.DIR);
        if (icon != null) {
            menu.setIcon(icon.name());
        }
        menu.setPerm(id);
        menu.setSeq(seq);

        sysMenuDao.save(menu);
    }


    public List<SysMenu> findByPerms(List<String> perms) {
        return sysMenuDao.findByPerms(perms);
    }
}
