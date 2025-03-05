
package io.tmgg.modules.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.config.external.MenuBadgeProvider;
import io.tmgg.config.external.UserMessageProvider;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Route;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.modules.sys.service.SysRoleService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class DefaultCommonController {

    @Resource
    SysRoleService roleService;

    @Resource
    SysMenuService sysMenuService;


    @Resource
    SysConfigService sysConfigService;


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

        Set<String> roles = subject.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            List<SysRole> roleList = roleService.findAllByCode(roles);
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


    @GetMapping("getMessageCount")
    public AjaxResult getMessageCount() {
        // 用户信息实现类，多个累加
        Map<String, UserMessageProvider> beans = SpringUtil.getBeansOfType(UserMessageProvider.class);

        int sum = 0;
        for (Map.Entry<String, UserMessageProvider> entry : beans.entrySet()) {
            UserMessageProvider provider = entry.getValue();
            int count = provider.count();
            sum += count;
        }

        return AjaxResult.ok().data(sum);
    }

    @GetMapping("getMessageList")
    public AjaxResult getMessageList() {
        // 用户信息实现类，多个累加
        Map<String, UserMessageProvider> beans = SpringUtil.getBeansOfType(UserMessageProvider.class);

        Map<String, List<UserMessageProvider.UserMessageVo>> data = new HashMap<>();
        for (Map.Entry<String, UserMessageProvider> entry : beans.entrySet()) {
            UserMessageProvider provider = entry.getValue();
            List<UserMessageProvider.UserMessageVo> list = provider.list();

            data.put(provider.userMsgType(), list);
        }

        // 排序 , 按消息数倒序
        List<String> keys = data.keySet().stream().sorted((o1, o2) -> data.get(o2).size() - data.get(o1).size()).collect(Collectors.toList());

        LinkedHashMap<String, List<UserMessageProvider.UserMessageVo>> sortData = new LinkedHashMap<>();
        for (String key : keys) {
            sortData.put(key, data.get(key));
        }


        return AjaxResult.ok().data(sortData);
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



        fillBadge(routes);


        TreeManager<Route> tm = new TreeManager<>(routes,Route::getId, Route::getPid, Route::getChildren, Route::setChildren);
        List<Route> tree = tm.getTree();

        Map<String, Route> treeMap = tm.getMap();
        tm.traverseTree(tree, item -> {
            if(item.getPid() == null){
                item.setRootId(item.getId());
            }else {
                Route parent = treeMap.get(item.getPid());
                if(parent != null){
                    item.setRootId(parent.getRootId());
                }
            }
        });


        Dict info = new Dict();

        List<Dict> topMenus = tree.stream().map(r -> Dict.of("key", r.getKey(), "label", r.getLabel())).toList();
        info.put("topMenus", topMenus);
        info.put("menus", tree);

        return AjaxResult.ok().data(info);
    }

    private static void fillBadge(List<Route> list) {
        // 角标, 右上角数字

        Map<String, Route> idMap = list.stream().collect(Collectors.toMap(Route::getId, r -> r));
        Map<String, Route> codeMap = list.stream().collect(Collectors.toMap(Route::getKey, r -> r));

        List<MenuBadgeProvider.MenuBadge> badges = new ArrayList<>();
        Collection<MenuBadgeProvider> menuBadgeProviders = SpringTool.getBeans(MenuBadgeProvider.class);
        for (MenuBadgeProvider badgeProvider : menuBadgeProviders) {
            badges.addAll(badgeProvider.getData());
        }

        for (MenuBadgeProvider.MenuBadge menuBadge : badges) {
            String code = menuBadge.getMenuCode();
            int badge = menuBadge.getBadge();

            Route r = codeMap.get(code);
            if (r == null || badge <= 0) {
                continue;
            }
            r.setBadge(badge);

            // 父节点累加
            Route parent = idMap.get(r.getPid());
            if (parent == null) {
                continue;
            }
            parent.setBadge(r.getBadge() + parent.getBadge());
        }
    }

}
