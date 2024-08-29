
package io.tmgg.sys;

import io.tmgg.SystemProperties;
import io.tmgg.config.external.MenuBadgeProvider;
import io.tmgg.config.external.UserMessageProvider;
import io.tmgg.lang.RequestTool;
import io.tmgg.lang.ResourceTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Route;
import io.tmgg.sys.auth.controller.LoginUserVo;
import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.sys.menu.service.SysMenuService;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.sys.watermask.service.WatermarkService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    SystemProperties systemProperties;

    @Resource
    WatermarkService watermarkService;


    /**
     * 站点信息， 非登录情况下使用
     *
     *
     */
    @PublicApi
    @GetMapping("site-info")
    public AjaxResult siteInfo() {
        Map<String, Object> data = BeanUtil.beanToMap(systemProperties, "captchaEnable", "copyright", "siteTitle");
        return AjaxResult.success(data);
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

        SystemProperties sysProps = new SystemProperties();
        BeanUtils.copyProperties(systemProperties, sysProps);
        vo.setSystemProperties(sysProps);


        // 水印
        vo.setWatermarkList(watermarkService.findValid());

        return AjaxResult.success(vo);
    }

    /**
     * 获取当前登录信息
     */
    @Deprecated
    @GetMapping("getLoginUser")
    public AjaxResult getLoginUser() {
        return getLoginInfo();
    }

    @Deprecated
    @PublicApi
    @GetMapping("initInfo")
    public AjaxResult initInfo() {
        System.err.println("改方法已废弃，请升级前端版本");
        return this.siteInfo();
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

        return AjaxResult.success(sum);
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


        return AjaxResult.success(sortData);
    }


    @PublicApi
    @GetMapping("ping")
    public AjaxResult ping(HttpServletRequest req) {
        String baseUrl = RequestTool.getBaseUrl(req);
        return AjaxResult.success("pong", baseUrl);
    }

    @PublicApi
    @GetMapping("sysAbout")
    public AjaxResult sysAbout() throws IOException {
        org.springframework.core.io.Resource about = ResourceTool.findOne(systemProperties.getAboutFile());
        String content = IoUtil.readUtf8(about.getInputStream());
        return AjaxResult.success("ok", content);
    }


    /**
     * 前端左侧菜单调用， 以展示顶部及左侧菜单
     */
    @GetMapping("appMenuTree")
    public AjaxResult appMenuTree() {
        Subject subject = SecurityUtils.getSubject();
        List<Route> list = sysMenuService.findAllAppMenuList();

        Map<String, SysMenu> map = sysMenuService.findMap();

            Collection<String> userMenuIds = new HashSet<>();

            // 过滤id
            for (SysMenu m : map.values()) {
                String perm = m.getPermission();
                if (subject.hasPermission(perm)) {
                    userMenuIds.add(m.getId());

                    // 父节点加入
                    SysMenu parent = map.get(m.getPid());
                    while (parent != null) {
                        userMenuIds.add(parent.getId());
                        parent = map.get(parent.getPid());
                    }
                }
            }

            list = list.stream().filter(r -> userMenuIds.contains(r.getId())).collect(Collectors.toList());


        {
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


        // 添加应用
        List<Route> appRoute = sysMenuService.getAppRoute();
        list.addAll(0, appRoute);


        List<Route> tree = TreeTool.buildTree(list);

        // 删除空应用（不包含任何功能的application)
        tree = tree.stream().filter(app -> CollUtil.isNotEmpty(app.getChildren())).collect(Collectors.toList());

        return AjaxResult.success(tree);
    }

}
