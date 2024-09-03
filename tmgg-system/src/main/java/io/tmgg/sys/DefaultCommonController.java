
package io.tmgg.sys;

import cn.hutool.core.util.ObjUtil;
import io.tmgg.SystemProperties;
import io.tmgg.config.external.MenuBadgeProvider;
import io.tmgg.config.external.UserMessageProvider;
import io.tmgg.lang.ResourceTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Route;
import io.tmgg.sys.controller.LoginUserVo;
import io.tmgg.sys.perm.SysPermService;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.sys.service.SysConfigService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class DefaultCommonController {

    @Resource
    SysRoleService roleService;

    @Resource
    SysPermService sysPermService;


    @Resource
    SysConfigService sysConfigService;



    /**
     * 站点信息， 非登录情况下使用
     */
    @PublicApi
    @GetMapping("site-info")
    public AjaxResult siteInfo() {
//        Integer count = (Integer) session.getAttribute("count");
//        count = ObjUtil.defaultIfNull(count, 0);
//        session.setAttribute("count", count + 1);
//        System.out.println("sessionId:"+ session.getId() + ",count="+session.getAttribute("count"));
        Map<String, String> siteInfo = sysConfigService.findSiteInfo();
        return AjaxResult.ok().data(siteInfo);
    }


    /**
     * 获取当前登录信息
     */
    @GetMapping("getLoginInfo")
    private AjaxResult getLoginInfo() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("subject account {}", subject.getAccount());


        LoginUserVo vo = new LoginUserVo();
        BeanUtil.copyProperties(subject, vo);


        Set<String> roles = subject.getRoles();

        if (!CollectionUtils.isEmpty(roles)) {
            List<SysRole> roleList = roleService.findAllByCode(roles);
            Set<String> roleNames = roleList.stream().map(SysRole::getName).collect(Collectors.toSet());

            vo.setRoleNames(StringUtils.join(roleNames, ","));
        }

        vo.setOrgName(subject.getUnitName());
        vo.setDeptName(subject.getDeptName());


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
    @GetMapping("menuTree")
    public AjaxResult menuTree() {
        Subject subject = SecurityUtils.getSubject();
        List<Route> list = sysPermService.findAllAppMenuList();

        list = list.stream().filter(r -> subject.hasPermission(r.getPerm())).collect(Collectors.toList());


        fillBadge(list);


        List<Route> tree = TreeTool.buildTree(list);

        return AjaxResult.ok().data(tree);
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
