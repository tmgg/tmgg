package io.tmgg.init;

import io.tmgg.lang.LongTool;
import io.tmgg.sys.perm.SysMenuDao;
import io.tmgg.sys.perm.SysMenu;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.MenuType;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PermissionToDatabaseHandler {


    @Resource
    private SysMenuDao menuDao;



    public void run() {
        log.info("开始分析代码中的权限码");


        //1.获取所有后端接口
        Map<String, RequestMappingHandlerMapping> mappingMap = SpringUtil.getApplicationContext().getBeansOfType(RequestMappingHandlerMapping.class);

        Collection<RequestMappingHandlerMapping> mappings = mappingMap.values();
        // mapping通常只有一个
        for (RequestMappingHandlerMapping mapping : mappings) {
            Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

            for (Map.Entry<RequestMappingInfo, HandlerMethod> e : map.entrySet()) {
                RequestMappingInfo info = e.getKey();
                HandlerMethod handlerMethod = e.getValue();

                if (!handlerMethod.hasMethodAnnotation(HasPermission.class)) {
                    continue;
                }

                HasPermission hasPermission = handlerMethod.getMethodAnnotation(HasPermission.class);

                String perm = hasPermission.value();

                if (StrUtil.isEmpty(perm)) {
                    perm = getPermByRequestMapping(info);
                }
                addMenu(perm);
            }
        }

    }

    @NotNull
    private static String getPermByRequestMapping(RequestMappingInfo info) {
        String perm;
        Set<String> patterns = info.getPathPatternsCondition().getPatterns().stream().map(PathPattern::getPatternString).collect(Collectors.toSet());
        Assert.state(patterns.size() == 1, "未指定 " + HasPermission.class.getSimpleName() + "的value时，url只能设置一个");

        perm = patterns.iterator().next();


        // 将url 的斜杠转为冒号
        String removePrefix = StrUtil.removePrefix(perm, "/");
        perm = removePrefix.replaceAll("/", ":");


        return perm;
    }


    private void addMenu(String perm) {
        SysMenu byPerm = menuDao.findByPerm(perm);
        if (byPerm != null) {
            return;
        }






        Assert.state(!perm.contains(","), "权限中不能包含逗号" + perm); //  旧版本支持，新版本不支持


        SysMenu old = menuDao.findByPerm(perm);

        SysMenu btn = old == null ? new SysMenu() : old;


        btn.setName(perm); // TODO
        btn.setPerm(perm);
        btn.setType(MenuType.BTN);

        String pid = StringUtils.substringBeforeLast(perm, ":");
        btn.setPid(pid);
        btn.setVisible(false);

        if (btn.getId() == null) {
            btn.setId(convertPermToId(perm));
        }

        menuDao.save(btn);
    }




    /**
     * 将权限码调整为固定ID，这样即使重新生成，也保证ID不变
     * <p>
     * long 8字节， 64位， 每次移动5位，可移动12次
     *
     * @param perm 如：sysUser:add
     *
     */
    private String convertPermToId(String perm) {
        return String.valueOf(LongTool.strToMd5Long(perm));
    }


}
