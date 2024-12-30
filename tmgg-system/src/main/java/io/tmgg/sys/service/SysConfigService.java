
package io.tmgg.sys.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import io.tmgg.SysProp;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.dao.SysConfigDao;
import io.tmgg.sys.entity.SysConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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


    @Override
    public SysConfig saveOrUpdate(SysConfig input) throws Exception {
        SysConfig old = dao.findOne(input);
        old.setValue(input.getValue());
        return dao.save(old);
    }

    public boolean getBoolean(String key) {
        Object value = this.findValue(key);
        return (boolean) value;
    }



    public Object findValue(String key) {
        SysConfig sysConfig = this.findOne(key);
        Assert.notNull(sysConfig, "系统配置不存在" + key);
        return parseFinalValue(sysConfig);
    }
    public String getStr(String key) {
        Assert.state(key.startsWith("sys."), "键必须已sys.开头");
        SysConfig sysConfig = this.findOne(key);
        if (sysConfig != null) {
            return (String) parseFinalValue(sysConfig);
        }
        return null;
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

    // 解析最终的值， 优先级 数据库value > spring属性 > 默认值
    private static Object parseFinalValue(SysConfig sysConfig) {
        String v = sysConfig.getValue();
        if (StrUtil.isEmpty(v)) {
            // 环境变量
            String propKey =  sysConfig.getId();
            propKey = StrUtil.toUnderlineCase(propKey).replace("_","-"); // 将大写转换为-

            String property = SpringTool.getProperty(propKey);
            if (StrUtil.isNotEmpty(property)) {
                v = property;
            }
        }

        if (StrUtil.isEmpty(v)) {
            v = sysConfig.getDefaultValue();
        }


        if ("boolean".equals(sysConfig.getValueType())) {
            return Boolean.valueOf(v);
        }


        return v;
    }


}
