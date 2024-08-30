
package io.tmgg.sys.perm;

import io.tmgg.SystemProperties;
import io.tmgg.core.event.LogoutEvent;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.obj.Route;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

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
    public List<Route> findAllAppMenuList() {
        List<SysPerm> sysPermList = sysPermDao.findMenuVisible();

        List<Route> routes = new LinkedList<>();


        for (SysPerm m : sysPermList) {
            String pid = m.getPid();

            // iframe设置完整url
            String url = m.getPath();

            Route route = new Route(String.valueOf(m.getId()), pid, m.getName(), url, null);
            route.setIcon(m.getIcon());
            route.setPerm(StrUtil.emptyToNull(m.getPerm()));
            route.setIframe(m.getIframe());
            routes.add(route);
        }

        return routes;
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


    @EventListener
    public void onLogout(LogoutEvent e) {
        log.info("退出系统事件 {}", e.getUserId());
    }


    public Map<String, SysPerm> findMap() {
        Map<String, SysPerm> map = new HashMap<>();
        List<SysPerm> all = this.findAll();
        for (SysPerm sysPerm : all) {
            map.put(sysPerm.getId(), sysPerm);
        }
        return map;
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
