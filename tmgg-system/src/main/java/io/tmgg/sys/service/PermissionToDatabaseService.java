package io.tmgg.sys.service;

import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.LongTool;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.sys.dao.SysMenuDao;
import io.tmgg.sys.entity.SysMenu;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.MenuType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PermissionToDatabaseService {


    @Resource
    private SysMenuDao menuDao;

    @Resource
    private JpaService jpaService;



    public void run() throws IOException, ClassNotFoundException {
        log.info("开始分析代码中的权限码");
        Map<String, String> dict = new CaseInsensitiveMap<>(); // 翻译
        {
            // 添加常见翻译
            dict.put("delete","删除");
            dict.put("page","列表");
            dict.put("list","列表");
            dict.put("save","增改");
            dict.put("disable","禁用");
            dict.put("disableAll","禁用所有");
            dict.put("enable","启用");
            dict.put("enableAll","启用所有");
            dict.put("export","导出");
        }


        List<Class<?>> entityList = jpaService.findAllClass();

        for (Class<?> cls : entityList) {
            String remark = RemarkTool.getRemark(cls);
            if (remark != null) {
                dict.put(cls.getSimpleName().toLowerCase(), remark);
            }
        }


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
                Remark remark = handlerMethod.getMethodAnnotation(Remark.class);


                String perm = hasPermission.value();

                if (StrUtil.isEmpty(perm)) {
                    perm = getPermByRequestMapping(info);
                }


                String permLabel = perm; // 权限显示名称
                if (remark != null) {
                    permLabel = remark.value();
                } else {
                    if(perm.contains(":")){
                        String option = StrUtil.subAfter(perm, ":", true);
                        if(dict.containsKey(option)){
                            permLabel = dict.get(option);
                        }
                    }
                }

                addMenu(perm, permLabel);
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


    private void addMenu(String perm, String permLabel) {
        SysMenu byPerm = menuDao.findByPerm(perm);
        if (byPerm != null) {
            return;
        }

        Assert.state(!perm.contains(","), "权限中不能包含逗号" + perm); //  旧版本支持，新版本不支持

        SysMenu old = menuDao.findByPerm(perm);

        SysMenu btn = old == null ? new SysMenu() : old;


        btn.setName(permLabel);
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
     */
    private String convertPermToId(String perm) {
        return String.valueOf(LongTool.strToMd5Long(perm));
    }


}
