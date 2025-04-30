
package io.tmgg.modules.sys;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Route;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.modules.sys.service.SysMenuBadgeService;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.modules.sys.service.SysRoleService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
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
public class CommonController {

    @Resource
    SysRoleService roleService;

    @Resource
    SysMenuService sysMenuService;


    @Resource
    SysConfigService sysConfigService;

    @Resource
    SysMenuBadgeService sysMenuBadgeService;


    /**
     * 站点信息， 非登录情况下使用
     */
    @PublicRequest
    @GetMapping("site-info")
    public AjaxResult siteInfo() {
        Map<String, Object> siteInfo = sysConfigService.findSiteInfo();
        return AjaxResult.ok().data(siteInfo);
    }


    /**
     * 获取当前登录信息
     */
    @GetMapping("getLoginInfo")
    private AjaxResult getLoginInfo() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("subject account {}", subject.getAccount());

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
            Assert.state(roleList.size() ==roleIds.size(),"用户角色已被修改，请重新登录");
            Set<String> roleNameSet = roleList.stream().map(SysRole::getName).collect(Collectors.toSet());
            String roleNames = StringUtils.join(roleNameSet, ",");
            vo.put("roleNames", roleNames);
        }

        return AjaxResult.ok().data(vo);
    }

    /**
     * 获取当前登录信息
     */
    @Deprecated
    @GetMapping("getLoginUser")
    public AjaxResult getLoginUser() {
        return getLoginInfo();
    }






    /**
     * 前端左侧菜单调用， 以展示顶部及左侧菜单
     */
    @GetMapping("menuInfo")
    public AjaxResult menuInfo() {
        Subject subject = SecurityUtils.getSubject();
        Map<String, SysMenu> map = sysMenuService.findMenuMap();


        List<SysMenu> list = map.values().stream().filter(r -> subject.hasPermission(r.getPerm())).toList();
        list = new ArrayList<>(list); // 调整为可变list

        // 将父节点（目录）也加入
        {
            Set<String> ids = new HashSet<>();
            for (SysMenu route : list) {
                SysMenu parent = map.get(route.getPid());
                while (parent != null) {
                    ids.add(parent.getId());
                    parent = map.get(parent.getPid());
                }
            }
            for (String id : ids) {
                list.add(map.get(id));
            }

        }


        // 去重,排序
        list = list.stream().distinct().sorted(Comparator.comparing(SysMenu::getSeq)).collect(Collectors.toList());


        List<Route> routes = new LinkedList<>();
        for (SysMenu m : list) {
            String pid = m.getPid();
            // iframe设置完整url
            String url = m.getPath();

            Route route = new Route(String.valueOf(m.getId()), pid, m.getName(), url, null);
            route.setIcon(m.getIcon());
            route.setPerm(StrUtil.emptyToNull(m.getPerm()));
            route.setIframe(m.getIframe());
            routes.add(route);
        }



        TreeManager<Route> tm = new TreeManager<>(routes,Route::getId, Route::getPid, Route::getChildren, Route::setChildren);
        List<Route> tree = tm.getTree();

        Map<String, Route> treeMap = tm.getMap();
        tm.traverseTree(tree, item -> {
            if(item.getPid() == null){
                item.setRootid(item.getId());
            }else {
                Route parent = treeMap.get(item.getPid());
                if(parent != null){
                    item.setRootid(parent.getRootid());
                }
            }
        });


        Dict info = new Dict();

        List<Dict> topMenus = tree.stream().map(r -> Dict.of("key", r.getKey(), "label", r.getLabel())).toList();
        info.put("topMenus", topMenus);
        info.put("menus", tree);
        info.put("badgeList", sysMenuBadgeService.findAll());

        return AjaxResult.ok().data(info);
    }

}
