
package io.tmgg.modules.system.service;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.common.AntDesignIcon;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.TreeTool;
import io.tmgg.web.enums.MenuType;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.lang.obj.TreeNode;
import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.system.dao.JsonEntityFileDao;
import io.tmgg.modules.system.dao.SysMenuDao;
import io.tmgg.modules.system.entity.JsonEntity;
import io.tmgg.modules.system.entity.SysMenu;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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





    @Transactional
    public void changeIcon(SysMenu input) throws Exception {
        SysMenu menu = sysMenuDao.findById(input.getId());
        menu.setIcon(input.getIcon());

        JsonEntity entity = jsonEntityFileDao.findOne(SysMenu.class, menu.getId());
        Assert.notNull(entity, "未找到数据文件");

        entity.getData().put("icon",input.getIcon());

        jsonEntityFileDao.save(entity);

    }

    @Transactional
    public void changeSeq(SysMenu input) throws Exception {
        SysMenu menu = sysMenuDao.findById(input.getId());
        Integer seq = input.getSeq();
        menu.setSeq(seq);

        JsonEntity entity = jsonEntityFileDao.findOne(SysMenu.class, menu.getId());
        Assert.notNull(entity, "未找到数据文件");

        entity.getData().put("seq",seq);

        jsonEntityFileDao.save(entity);
    }

    public List<SysMenu> findAllAndParent(List<String> menuIds) {
        List<SysMenu> list = sysMenuDao.findAll();

        TreeManager<SysMenu> tm = new TreeManager<>(list, BaseEntity::getId, SysMenu::getPid, SysMenu::getChildren, SysMenu::setChildren);

        Set<String> ids = new HashSet<>();
        for (String menuId : menuIds) {
            ids.addAll(tm.getParentIdListById(menuId));
        }
        ids.addAll(menuIds);

        return list.stream().filter(t->ids.contains(t.getId())).collect(Collectors.toList());
    }


    public void addMenu(String pid, String id, String name, AntDesignIcon icon, String perm, String path, int seq,boolean refreshOnTabClick) {
        SysMenu menu = sysMenuDao.findOne(id);
        if(menu == null){
            menu = new SysMenu();
        }

        menu.setId(id);
        menu.setPid(pid);
        menu.setName(name);
        menu.setPerm(perm);
        menu.setPath(path);
        menu.setRefreshOnTabClick(refreshOnTabClick);
        menu.setSeq(seq);
        if(icon != null){
            menu.setIcon(icon.name());

        }

        sysMenuDao.save(menu);
    }

    public void addDir(String pid, String id, String name, AntDesignIcon icon, int seq) {
        SysMenu menu = sysMenuDao.findOne(id);
        if(menu == null){
            menu = new SysMenu();
        }

        menu.setId(id);
        menu.setPid(pid);
        menu.setName(name);
        menu.setType(MenuType.DIR);
        if(icon != null){
            menu.setIcon(icon.name());
        }

        menu.setSeq(seq);

        sysMenuDao.save(menu);
    }
}
