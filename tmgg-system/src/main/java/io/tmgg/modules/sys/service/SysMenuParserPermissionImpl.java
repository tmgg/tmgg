package io.tmgg.modules.sys.service;

import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.LongTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.ann.MsgTool;
import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.sys.dao.SysMenuDao;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.MenuType;
import io.tmgg.web.perm.PermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class SysMenuParserPermissionImpl implements SysMenuParser {


    @Resource
    private SysMenuDao menuDao;

    @Resource
    private JpaService jpaService;

    @Resource
    private PermissionService permissionService;


    @Override
    public Collection<SysMenu> getMenuList() throws Exception {
        log.info("开始分析代码中的权限码");

        Set<SysMenu> result = new HashSet<>();

        Map<String, String> dict = getDict();


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


                Msg msg = handlerMethod.getMethodAnnotation(Msg.class);

                String perm = permissionService.parsePerm(handlerMethod, info);




                String permLabel = perm; // 权限显示名称
                if (msg != null) {
                    permLabel = msg.value();
                } else {
                    if(perm.contains(":")){
                        String option = StrUtil.subAfter(perm, ":", true);
                        if(dict.containsKey(option)){
                            permLabel = dict.get(option);
                        }
                    }
                }

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


    private Map<String, String> getDict() throws IOException, ClassNotFoundException {
        Map<String, String> dict = new CaseInsensitiveMap<>(); // 翻译
        {
            // 添加常见翻译
            dict.put("delete","删除");
            dict.put("batchDelete","批量删除");
            dict.put("page","列表");
            dict.put("list","列表");
            dict.put("save","增改");
            dict.put("disable","禁用");
            dict.put("disableAll","禁用所有");
            dict.put("enable","启用");
            dict.put("enableAll","启用所有");
            dict.put("export","导出");
            dict.put("exportExcel","导出Excel");

            dict.put("sync","同步");
            dict.put("batchSave","批量增改");
            dict.put("reset","重置");
            dict.put("status","查看状态");
            dict.put("detail","查看");
            dict.put("get","查看");
        }


        List<Class<?>> entityList = jpaService.findAllClass();

        for (Class<?> cls : entityList) {
            String remark = MsgTool.getMsg(cls);
            if (remark != null) {
                dict.put(cls.getSimpleName().toLowerCase(), remark);
            }
        }
        return dict;
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
