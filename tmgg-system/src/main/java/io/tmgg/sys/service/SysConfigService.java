
package io.tmgg.sys.service;

import cn.hutool.system.SystemUtil;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.sys.dao.SysConfigDao;
import io.tmgg.sys.entity.SysConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
public class SysConfigService extends BaseService<SysConfig> {

    @Resource
    private SysConfigDao dao;

    @Override
    public SysConfig saveOrUpdate(SysConfig input) throws Exception {
        SysConfig old = baseDao.findOne(input);
        old.setValue(input.getValue());
        return baseDao.save(old);
    }

    public boolean getBoolean(String key) {
        Object value = dao.findValue(key);
        return (boolean) value;
    }

    public String getStr(String key) {
        return (String) dao.findValue(key);
    }


    /**
     * 获取默认密码
     */
    public String getDefaultPassWord() {
        return getStr("default_password");
    }

    /**
     * 获取自定义的windows或linux环境本地文件上传路径
     */
    public String getFileUploadPath() {
        boolean isWin = SystemUtil.getOsInfo().isWindows();
        String key = isWin ? "file_upload_path_for_windows" : "file_upload_path_for_linux";

        return getStr(key);
    }


    public boolean getMultiDeviceLogin() {
        return getBoolean("multi_device_login");
    }


    public Map<String, Object> findSiteInfo() {
        return dao.findByPrefix("siteInfo");
    }

}
