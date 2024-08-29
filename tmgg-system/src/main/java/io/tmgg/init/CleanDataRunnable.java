package io.tmgg.init;


import io.tmgg.SystemProperties;
import io.tmgg.dbtool.DbTool;
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
    DbTool dbTool;

    @Resource
    SystemProperties systemProperties;

    @Override
    public void run()  {
        if(systemProperties.isMenuAutoUpdate()){
            log.info("开始清空 sys_app，sys_menu");
            dbTool.update("SET FOREIGN_KEY_CHECKS = 0");
            dbTool.update("delete from sys_menu");
            dbTool.update("SET FOREIGN_KEY_CHECKS = 1");
        }
    }



}
