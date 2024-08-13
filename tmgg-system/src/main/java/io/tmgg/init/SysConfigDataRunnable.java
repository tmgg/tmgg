package io.tmgg.init;

import io.tmgg.sys.consts.dao.SysConfigDao;
import io.tmgg.sys.consts.entity.SysConfig;
import cn.hutool.core.util.RandomUtil;
import io.tmgg.sys.consts.SysConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 根据枚举，自动生成数据字典
 */
@Component
@Slf4j
public class SysConfigDataRunnable implements CommandLineRunner {


    @Resource
    SysConfigDao dao;


    @Override
    public void run(String... args) throws Exception {
        log.info("-----------------------------------------------------------");
        {
            log.info("初始化系统配置数据...");
            dao.initDefault(SysConfigConstants.SESSION_EXPIRE, "会话过期时间", "7200", "session会话过期时间（单位：秒）");
            dao.initDefault(SysConfigConstants.DEFAULT_PASSWORD, "默认密码", RandomUtil.randomString(12), "用户的默认密码");
            dao.initDefault(SysConfigConstants.FILE_UPLOAD_PATH_FOR_WINDOWS, "win本地上传文件路径", "d:/tmp", null);
            dao.initDefault(SysConfigConstants.FILE_UPLOAD_PATH_FOR_LINUX, "linux/mac本地上传文件路径", "/home/files", null);
            dao.initDefault(SysConfigConstants.ENABLE_SINGLE_LOGIN, "单用户登陆的开关", "false", "填写true | false");
        }

        log.info("-----------------------------------------------------------");
    }
}
