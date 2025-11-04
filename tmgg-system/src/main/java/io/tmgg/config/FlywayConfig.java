package io.tmgg.config;

import io.tmgg.init.SystemHook;
import io.tmgg.init.SystemHookEventType;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * flyway默认在jpa建表前执行，这里
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
    public SystemHook hook(Flyway flyway){
        return  new SystemHook() {
            @Override
            public void onEvent(SystemHookEventType eventType) {
                if(eventType == SystemHookEventType.BEFORE_DATA_INIT){
                    flyway.migrate();
                }
            }
        };
    }



}
