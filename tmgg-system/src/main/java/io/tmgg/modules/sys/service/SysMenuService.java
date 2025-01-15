
package io.tmgg.modules.sys.service;

import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.obj.TreeNode;
import io.tmgg.modules.sys.dao.SysMenuDao;
import io.tmgg.modules.sys.entity.SysMenu;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private JsonToDatabaseService jsonToDatabaseService;

    @Resource
    private PermissionToDatabaseService permissionToDatabaseService;


    /**
     * 不含按钮 及 不显示的东西
     */
    public Map<String, SysMenu> findMenuMap() {
        List<SysMenu> sysMenuList = sysMenuDao.findMenuVisible();
        return sysMenuList.stream().collect(Collectors.toMap(BaseEntity::getId, t -> t));
    }


    public List<TreeNode> treeForGrant() {
        List<SysMenu> all = sysMenuDao.findAllValid();

        Collection<TreeNode> nodes = new ArrayList<>();


        return convertTreeNode(all, nodes);
    }

    public List<TreeNode> menuTree() {
        List<SysMenu> all = sysMenuDao.findMenuVisible();

        Collection<TreeNode> nodes = new ArrayList<>();


        return convertTreeNode(all, nodes);
    }


    public  List<TreeNode> convertTreeNode(List<SysMenu> all, Collection<TreeNode> nodes) {
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


    public void reset() throws IOException, ClassNotFoundException {
        List<SysMenu> list = sysMenuDao.findAll();
        for (SysMenu sysMenu : list) {
            try {
                sysMenuDao.deleteById(sysMenu.getId());
            } catch (Exception e) {
                log.error("删除菜单失败");
            }
        }


        jsonToDatabaseService.cleanCache();
        jsonToDatabaseService.parseAndSave(SysMenu.class);
        permissionToDatabaseService.run();

    }


}
