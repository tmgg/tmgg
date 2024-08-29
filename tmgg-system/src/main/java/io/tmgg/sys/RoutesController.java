
package io.tmgg.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class RoutesController {

    @Resource
    SysRoleService roleService;

    @Resource
    SysMenuService sysMenuService;

    @Resource
    SystemProperties systemProperties;

    @Resource
    WatermarkService watermarkService;





    /**
     * 所有的路由，给前端用的
     * @return
     */
    @PublicApi
    @GetMapping("routes")
    public AjaxResult routes() {
        List<SysMenu> list = sysMenuService.findAll();

        List<UmiRoute> routes = list.stream().filter(m -> StrUtil.isNotEmpty(m.getRouter()))
                .map(m -> {
                    String path = m.getRouter();
                    UmiRoute umiRoute = new UmiRoute();
                    umiRoute.setPath(path);

                    return umiRoute;

                }).collect(Collectors.toList());

        return AjaxResult.ok().data(routes);
    }

    @Getter
    @Setter
    public static class UmiRoute {
        String path;
        String packageName;
    }
}
