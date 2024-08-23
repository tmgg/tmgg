package io.tmgg.init;

import io.tmgg.lang.LongTool;
import io.tmgg.SystemProperties;
import io.tmgg.sys.menu.dao.SysMenuDao;
import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.CommonStatus;
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
public class MenuInCodeRunnable implements Runnable {


    @Resource
    private SysMenuDao menuDao;

    @Resource
    private SystemProperties systemProperties;

    private static Map<String, String> DICT = new HashMap<>();

    {
        DICT.put("save", "增改");
        DICT.put("add", "新增");
        DICT.put("page", "列表");
        DICT.put("edit", "编辑");
        DICT.put("view", "查看");
        DICT.put("export", "导出");
        DICT.put("grant", "授权");
        DICT.put("role", "角色");
        DICT.put("own", "拥有");
        DICT.put("data", "数据");
        DICT.put("update", "更新");
        DICT.put("info", "信息");
        DICT.put("pwd", "密码");
        DICT.put("change", "修改");
        DICT.put("status", "状态");
        DICT.put("reset", "重置");
        DICT.put("tree", "树状列表");
        DICT.put("setAsDefault", "设为默认");
        DICT.put("delete", "删除");
        DICT.put("detail", "详情");
        DICT.put("report", "报表");
        DICT.put("change_status", "调整状态");
        DICT.put("submit", "提交");
        DICT.put("check", "审核");
        DICT.put("uncheck", "反审");
        DICT.put("deploy", "部署");
        DICT.put("save_content", "保存内容");
        DICT.put("reset_pwd", "重置密码");
        DICT.put("list", "列表");
        DICT.put("get", "详情");
        DICT.put("entry_page", "查看明细");
        DICT.put("grant_data", "授权数据");
    }

    private void clean() {
        DICT.clear();
        DICT = null;
    }

    @Override
    public void run() {
        log.info("开始分析代码中的权限码");
        if (!systemProperties.isMenuAutoUpdate()) {
            log.info("系统配置不用自动更新菜单，返回");
            this.clean();
            return;
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

                String perm = hasPermission.value();
                String label = hasPermission.title();

                if (StrUtil.isEmpty(perm)) {
                    perm = getPermByRequestMapping(info);
                }
                addMenu(perm, label);
            }
        }

        this.clean();
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


    private void addMenu(String perm, String label) {
        SysMenu byPerm = menuDao.findByPerm(perm);
        if (byPerm != null) {
            return;
        }

        if (StrUtil.isEmpty(label)) {
            label = translate(StringUtils.substringAfterLast(perm, ":"));
        }


        SysMenu parentMenu = getParent(perm);
        Assert.state(parentMenu != null, String.format("权限码：%s, 父菜单编码不存在，请检查菜单", perm));

        Assert.state(!perm.contains(","), "权限中不能包含逗号" + perm); //  旧版本支持，新版本不支持


        SysMenu old = menuDao.findByCode(perm);

        SysMenu btn = old == null ? new SysMenu() : old;


        btn.setName(label);
        btn.setPermission(perm);
        btn.setStatus(CommonStatus.ENABLE);
        btn.setType(MenuType.BTN);

        btn.setPid(parentMenu.getId());
        btn.setVisible("N");

        btn.setApplication(parentMenu.getApplication());
        btn.setSeq(0);
        btn.setRemark("通过代码中注解生成" + HasPermission.class.getSimpleName());

        if (btn.getId() == null) {
            btn.setId(convertPermToId(perm));
        }

        menuDao.save(btn);
    }

    private SysMenu getParent(String perm) {
        Assert.state(StrUtil.count(perm,":") <= 1, "权限码中最多有1个分号,当前权限码为" + perm);
        String parentCode = StringUtils.substringBeforeLast(perm, ":");
        SysMenu parentMenu = menuDao.findByCode(parentCode);

        return parentMenu;
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


    private String translate(String en) {
        String str = StrUtil.toUnderlineCase(en);

        String[] arr = str.split("[-:]");


        List<String> list = Arrays.stream(arr).map(a -> {
            String res = DICT.get(a);
            return StringUtils.defaultString(res, a);
        }).collect(Collectors.toList());

        return StringUtils.join(list, "");
    }


}
