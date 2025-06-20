package io.tmgg.modules.system.service;

import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.LongTool;
import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.MenuType;
import io.tmgg.framework.perm.PermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Component
@Slf4j
public class SysMenuParserPermissionImpl implements SysMenuParser {


    @Resource
    private PermissionService permissionService;


    @Override
    public Collection<SysMenu> parseMenuList() throws Exception {
        log.info("开始分析代码中的权限码");

        Set<SysMenu> result = new HashSet<>();



        //1.获取所有后端接口
        Map<String, RequestMappingHandlerMapping> mappingMap = SpringUtil.getApplicationContext().getBeansOfType(RequestMappingHandlerMapping.class);

        Collection<RequestMappingHandlerMapping> mappings = mappingMap.values();
        // mapping通常只有一个
        for (RequestMappingHandlerMapping mapping : mappings) {
            Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

            for (Map.Entry<RequestMappingInfo, HandlerMethod> e : map.entrySet()) {
                RequestMappingInfo info = e.getKey();
                HandlerMethod handlerMethod = e.getValue();
                HasPermission hasPermission = handlerMethod.getMethodAnnotation(HasPermission.class);
                if(hasPermission == null){
                    continue;
                }

                String perm = permissionService.parsePerm(handlerMethod, info);

                String permLabel = permissionService.parsePermLabel(perm, hasPermission);

                // 构造btn
                {
                    Assert.state(!perm.contains(","), "权限中不能包含逗号" + perm); //  旧版本支持，新版本不支持
                    SysMenu btn = new SysMenu();
                    btn.setName(permLabel);
                    btn.setPerm(perm);
                    btn.setType(MenuType.BTN);

                    String pid = StringUtils.substringBeforeLast(perm, ":");
                    btn.setPid(pid);
                    btn.setVisible(false);
                    btn.setId(convertPermToId(perm));
                    result.add(btn);
                }
            }
        }

        return result;
    }




    /**
     * 将权限码调整为固定ID，这样即使重新生成，也保证ID不变
     * <p>
     * long 8字节， 64位， 每次移动5位，可移动12次
     *
     * @param perm 如：sysUser:add
     */
    private String convertPermToId(String perm) {
        return String.valueOf(LongTool.strToMd5Long(perm));
    }



}
