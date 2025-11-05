package io.tmgg.config;

import io.tmgg.init.SystemHook;
import io.tmgg.init.SystemHookEventType;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * flyway默认在jpa建表前执行，这里调整下顺序。
 * 实现思路：
 * 1 flyway默认初始化时不执行操作
 * 2 等待框架级别的事件出发
 */
@Configuration
public class FlywayConfig implements SystemHook {
    @Bean
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway, (f) -> {
            // do noting
        });
    }


    @Bean
    public SystemHook flywayHook(Flyway springFlyway, DataSource ds){
        return  new SystemHook() {
            @Override
            public void onEvent(SystemHookEventType eventType) {
                if(eventType == SystemHookEventType.BEFORE_DATA_INIT){

                    // 执行框架的flyway, 放在不同的文件夹下的
                    Flyway flyway = Flyway.configure()
                            .dataSource(ds)
                            .locations("classpath:db/migration-framework")
                            .baselineOnMigrate(true)
                            .baselineVersion("0")
                            .table("flyway_schema_history_framework")
                            .load();
                    flyway.migrate();


                    // 执行springboot 自动配置的flyway
                    springFlyway.migrate();
                }
            }
        };
    }



}
