
package io.tmgg.sys.perm;

import io.tmgg.SysProp;
import io.tmgg.dbtool.DbTool;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
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
    private SysProp sysProp;

    @Resource
    private DbTool db;



    /**
     * 不含按钮 及 不显示的东西
     */
    public Map<String, SysMenu> findMenuMap() {
        List<SysMenu> sysMenuList = sysMenuDao.findMenuVisible();


        return sysMenuList.stream().collect(Collectors.toMap(BaseEntity::getId, t->t));
    }


    public List<MenuTreeNode> treeForGrant() {
        List<SysMenu> all = sysMenuDao.findAllValid();

        Collection<MenuTreeNode> nodes = new ArrayList<>();




        for (SysMenu sysMenu : all) {
            MenuTreeNode menuTreeNode = new MenuTreeNode();
            menuTreeNode.setId(sysMenu.getId());
            menuTreeNode.setPid(sysMenu.getPid());
            menuTreeNode.setValue(sysMenu.getId());
            String titleAddon = StrUtil.isEmpty(sysMenu.getPerm()) ? "" : " [" + sysMenu.getPerm() + "]";
            menuTreeNode.setTitle(sysMenu.getName() + titleAddon);
            menuTreeNode.setWeight(sysMenu.getSeq());
            nodes.add(menuTreeNode);
        }

        List<MenuTreeNode> tree = TreeTool.buildTree(nodes);
        return tree;
    }








    public void init() {
        log.info("开始清空权限菜单表");

        String sql = """
                SET FOREIGN_KEY_CHECKS=0;
                truncate table  sys_menu;
                SET FOREIGN_KEY_CHECKS=1;""";
        int[] batchResult = db.batch(sql, null);
        log.info("清空结果 {}",batchResult);
    }
}
