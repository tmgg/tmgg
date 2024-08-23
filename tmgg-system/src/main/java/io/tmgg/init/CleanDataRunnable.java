package io.tmgg.init;

import io.tmgg.lang.jdbc.Jdbc;
import io.tmgg.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 升级时需要的一些处理，如表字段处理
 */
@Component
@Slf4j
public class CleanDataRunnable implements Runnable {


    @Resource
    Jdbc ndb;

    @Resource
    SystemProperties systemProperties;

    @Override
    public void run()  {
        if(systemProperties.isMenuAutoUpdate()){
            log.info("开始清空 sys_app，sys_menu");
            ndb.update("SET FOREIGN_KEY_CHECKS = 0");
            ndb.update("delete from sys_app");
            ndb.update("delete from sys_menu");
            ndb.update("SET FOREIGN_KEY_CHECKS = 1");
        }
    }



}
