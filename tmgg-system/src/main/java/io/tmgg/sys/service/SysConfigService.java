
package io.tmgg.sys.service;

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
        return  baseDao.save(old);
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
     * 获取自定义的windows环境本地文件上传路径
     */
    public String getFileUploadPathForWindows() {
        return getStr("file_upload_path_for_windows");
    }

    /**
     * 获取自定义的linux环境本地文件上传路径
     */
    public String getFileUploadPathForLinux() {
        return getStr("file_upload_path_for_linux");
    }


    public boolean getMultiDeviceLogin(){
        return getBoolean("multi_device_login");
    }



    public Map<String, Object> findSiteInfo() {
        return dao.findByPrefix("siteInfo");
    }
}
