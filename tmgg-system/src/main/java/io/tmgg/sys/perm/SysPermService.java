
package io.tmgg.sys.perm;

import io.tmgg.SystemProperties;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.obj.Route;
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
public class SysPermService extends BaseService<SysPerm> {


    @Resource
    private SysPermDao sysPermDao;

    @Resource
    private SystemProperties systemProperties;



    /**
     * 不含按钮 及 不显示的东西
     */
    public Map<String, SysPerm> findMenuMap() {
        List<SysPerm> sysPermList = sysPermDao.findMenuVisible();


        return sysPermList.stream().collect(Collectors.toMap(BaseEntity::getId, t->t));
    }


    public List<MenuTreeNode> treeForGrant() {
        List<SysPerm> all = sysPermDao.findAllValid();

        Collection<MenuTreeNode> nodes = new ArrayList<>();




        for (SysPerm sysPerm : all) {
            MenuTreeNode menuTreeNode = new MenuTreeNode();
            menuTreeNode.setId(sysPerm.getId());
            menuTreeNode.setPid(sysPerm.getPid());
            menuTreeNode.setValue(sysPerm.getId());
            String titleAddon = StrUtil.isEmpty(sysPerm.getPerm()) ? "" : " [" + sysPerm.getPerm() + "]";
            menuTreeNode.setTitle(sysPerm.getName() + titleAddon);
            menuTreeNode.setWeight(sysPerm.getSeq());
            nodes.add(menuTreeNode);
        }

        List<MenuTreeNode> tree = TreeTool.buildTree(nodes);
        return tree;
    }








    public void init() {
        System.err.println("自定更新权限菜单：" + systemProperties.isMenuAutoUpdate());
        if(!systemProperties.isMenuAutoUpdate()){
            return;
        }

        System.err.println("开始清空权限菜单表");
        sysPermDao.deleteAll();
    }
}
