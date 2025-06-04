package io.tmgg.framework.perm;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.modules.sys.dao.SysDictDao;
import io.tmgg.modules.sys.dao.SysDictItemDao;
import io.tmgg.modules.sys.entity.SysDict;
import io.tmgg.modules.sys.entity.SysDictItem;
import io.tmgg.modules.sys.service.JpaService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.pattern.PathPattern;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionService {

    public static final String DICK_CODE = "permLabel";
    @Resource
    SysDictDao sysDictDao;
    @Resource
    SysDictItemDao sysDictItemDao;

    @Resource
    JpaService jpaService;
    private SysDict dict;


    public   void init() throws IOException, ClassNotFoundException {
         this.dict = sysDictDao.findByCode(DICK_CODE);
        if (dict == null) {
            dict = new SysDict();
            dict.setCode(DICK_CODE);
            dict.setText("权限翻译");
            dict.setIsNumber(false);
            dict.setId(DICK_CODE);
            sysDictDao.save(dict);
        }

        // 添加常见翻译
        setPermLabel("delete", "删除");
        setPermLabel("batchDelete", "批量删除");
        setPermLabel("page", "列表");
        setPermLabel("list", "列表");
        setPermLabel("add", "增加");
        setPermLabel("edit", "编辑");
        setPermLabel("save", "增改");
        setPermLabel("update", "更新");
        setPermLabel("config", "配置");
        setPermLabel("submit", "提交");

        setPermLabel("start", "启动");
        setPermLabel("stop", "停止");


        setPermLabel("disable", "禁用");
        setPermLabel("disableAll", "禁用所有");
        setPermLabel("enable", "启用");
        setPermLabel("enableAll", "启用所有");
        setPermLabel("export", "导出");
        setPermLabel("exportExcel", "导出Excel");

        setPermLabel("sync", "同步");
        setPermLabel("batchSave", "批量保存");
        setPermLabel("reset", "重置");
        setPermLabel("status", "查看状态");
        setPermLabel("detail", "查看详情");
        setPermLabel("get", "查看");
        setPermLabel("info", "查看信息");
        setPermLabel("deploy", "部署");
        setPermLabel("preview", "预览");

        setPermLabel("design", "设计");
        setPermLabel("viewData", "查看数据");


        // 添加实体的翻译
        List<Class<?>> entityList = jpaService.findAllClass();
        for (Class<?> cls : entityList) {
            String entityMsg = RemarkTool.getMsg(cls);
            if (entityMsg != null) {
                setPermLabel(StrUtil.lowerFirst(cls.getSimpleName()), entityMsg);
            }
        }
    }


    public void setPermLabel(String code, String text) {
        String old = getPermLabel(code);
        if (old == null) {
            SysDictItem item = new SysDictItem();
            item.setBuiltin(true);
            item.setCode(code);
            item.setText(text);
            item.setEnabled(true);
            item.setSysDict(dict);
            sysDictItemDao.save(item);
        }
    }

    public String getPermLabel(String code) {
        return sysDictItemDao.findText(DICK_CODE, code);
    }

    private String getBtnCode(String perm) {
        if (perm.contains(":")) {
            String code = StrUtil.subAfter(perm, ":", true);
            return code;
        }
        return perm;
    }

    public String parsePermLabel(String perm, HasPermission ann) {
        String btnCode = getBtnCode(perm);

        String permLabel = ann.label();
        if (StrUtil.isNotEmpty(permLabel)) {
            setPermLabel(btnCode, permLabel);
            return permLabel;
        }

        permLabel = getPermLabel(btnCode);
        if (StrUtil.isNotEmpty(permLabel)) {
            return permLabel;
        }

        Assert.notNull(permLabel, "请给权限" + perm + "增加中文说明,如(@HasPermission(label=xxx)");

        return null;
    }

    public String parsePerm(HandlerMethod handlerMethod, RequestMappingInfo info) {
        Set<String> patterns = info.getPathPatternsCondition().getPatterns().stream().map(PathPattern::getPatternString).collect(Collectors.toSet());

        String url = patterns.iterator().next();

        return parsePerm(handlerMethod, url);
    }

    public String parsePerm(HandlerMethod handlerMethod, String url) {
        HasPermission hasPermission = handlerMethod.getMethodAnnotation(HasPermission.class);
        return parsePerm(hasPermission, url);
    }

    public String parsePerm(HasPermission hasPermission, String url) {
        if (hasPermission == null) {
            return null;
        }
        String perm = hasPermission.value();

        if (StrUtil.isNotEmpty(perm)) {
            return perm;
        }


        // 将url 的斜杠转为冒号
        url = StrUtil.removePrefix(url, "/");
        url = StrUtil.removeSuffix(url, "/");

        perm = url.replaceAll("/", ":");

        return perm;
    }

}
