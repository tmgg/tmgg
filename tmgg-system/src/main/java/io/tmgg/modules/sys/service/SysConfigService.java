
package io.tmgg.modules.sys.service;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import io.tmgg.lang.RequestTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.dao.SysConfigDao;
import io.tmgg.modules.sys.entity.SysConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class SysConfigService extends BaseService<SysConfig> {

    @Resource
    private SysConfigDao dao;

    public String getBaseUrl() {
        return this.getStr("sys.baseUrl");
    }

    /**
     * 获取基础路径，如果系统配置没有，则从request解析
     * @param request
     * @return
     */
    public String getOrParseBaseUrl(HttpServletRequest request) {
        String baseUrl = getBaseUrl();
        if (StrUtil.isBlank(baseUrl)) {
            baseUrl = RequestTool.getBaseUrl(request);
        }
        return baseUrl;
    }


    @Override
    public SysConfig saveOrUpdate(SysConfig input) throws Exception {
        SysConfig old = dao.findById(input.getId());
        old.setValue(input.getValue());
        return dao.save(old);
    }

    public boolean getBoolean(String key) {
        validateKey(key);
        Object value = this.findValue(key);
        return (boolean) value;
    }

    public void setBoolean(String key, boolean b) {
        SysConfig sysConfig = this.findOne(key);
        Assert.notNull(sysConfig, "配置不存在" + key);
        sysConfig.setValue(String.valueOf(b));
        dao.save(sysConfig);
    }

    public Object findValue(String key) {
        validateKey(key);
        SysConfig sysConfig = this.findOne(key);
        Assert.notNull(sysConfig, "系统配置不存在" + key);
        return parseFinalValue(sysConfig);
    }


    public String getStr(String key) {
        validateKey(key);
        SysConfig sysConfig = this.findOne(key);
        if (sysConfig != null) {
            return (String) parseFinalValue(sysConfig);
        }
        return null;
    }

    /**
     * 判断某个key是否有已经配置了值（默认值不算）
     * @return
     */
    public boolean isValueSet(String... keys) {
        JpaQuery<SysConfig> q = new JpaQuery<>();
        q.in("id", keys);

        long count = dao.count(q);
        return count == keys.length;
    }

    private static void validateKey(String key) {
       // Assert.state(Validator.isLetter(key) , "键必须已sys.开头");
    }

    /**
     * 获取默认密码
     */
    public String getDefaultPassWord() {
        return getStr("sys.default.password");
    }

    /**
     * 获取自定义的windows或linux环境本地文件上传路径
     */
    public String getFileUploadPath() {
        boolean isWin = SystemUtil.getOsInfo().isWindows();
        String key = isWin ? "sys.fileUploadPath.windows" : "sys.fileUploadPath.linux";

        return getStr(key);
    }


    public boolean getMultiDeviceLogin() {
        return getBoolean("sys.multiDeviceLogin");
    }


    public Map<String, Object> findSiteInfo() {
        return this.findByPrefix("sys.siteInfo");
    }


    /**
     * 通过前缀查询键值对
     *
     * @param prefix
     * @return
     */
    public Map<String, Object> findByPrefix(String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        JpaQuery<SysConfig> q = new JpaQuery<>();
        q.like("id", prefix + "%");
        List<SysConfig> list = this.findAll(q);

        Map<String, Object> map = new HashMap<>();
        for (SysConfig sysConfig : list) {
            String k = sysConfig.getId();
            k = k.replace(prefix, "");
            Object v = parseFinalValue(sysConfig);
            map.put(k, v);
        }

        return map;
    }

    // 解析最终的值， 优先级 数据库value >  默认值
    private static Object parseFinalValue(SysConfig sysConfig) {
        String v = sysConfig.getValue();
        if (StrUtil.isEmpty(v)) {
            v = sysConfig.getDefaultValue();
        }
        if ("boolean".equals(sysConfig.getValueType())) {
            return Boolean.valueOf(v);
        }
        return v;
    }


}
