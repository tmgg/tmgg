
package io.tmgg.sys.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.sys.dao.SysConfigDao;
import io.tmgg.sys.entity.SysConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// config 比较特殊，为了保证可控（如dao层缓存）， 不继承BaseService
@Service
@Slf4j
public class SysConfigService extends BaseService<SysConfig> {

    @Resource
    private SysConfigDao dao;

    public String get(String code) {
        return dao.findValueByCode(code);
    }


    public void set(String code, String value) {
        SysConfig config = findByCode(code);
        Assert.state(config != null, "配置未定义" + code);
        config.setValue(value);
        dao.save(config);
    }


    public SysConfig findByCode(String code) {
        return dao.findByCode(code);
    }


    /**
     * 获取默认密码
     */
    public String getDefaultPassWord() {
        return get("DEFAULT_PASSWORD");
    }

    /**
     * 获取自定义的windows环境本地文件上传路径
     */
    public String getDefaultFileUploadPathForWindows() {
        return getSysConfigWithDefault("FILE_UPLOAD_PATH_FOR_WINDOWS", String.class, "");
    }

    /**
     * 获取自定义的linux环境本地文件上传路径
     */
    public String getDefaultFileUploadPathForLinux() {
        return getSysConfigWithDefault("FILE_UPLOAD_PATH_FOR_LINUX", String.class, "");
    }

    /**
     * 获取是否允许单用户登陆的开关
     */
    public Boolean getEnableSingleLogin() {
        return getSysConfigWithDefault("SINGLE_LOGIN", Boolean.class, false);
    }


    /**
     * 获取config表中的配置，如果为空返回默认值
     */
    public <T> T getSysConfigWithDefault(String configCode, Class<T> clazz, T defaultValue) {
        String configValue = dao.findValueByCode(configCode);
        if (ObjectUtil.isEmpty(configValue)) {
            return defaultValue;
        }

        return Convert.convert(clazz, configValue);
    }


    public Map<String, String> findSiteInfo() {
        List<SysConfig> list = dao.findAll();

        String prefix = "siteInfo.";
        return list.stream().filter(s -> s.getKey().startsWith(prefix)).collect(Collectors.toMap(t -> t.getKey().substring(prefix.length()), t->StrUtil.emptyToDefault(t.getValue(), t.getDefaultValue())));

    }
}
