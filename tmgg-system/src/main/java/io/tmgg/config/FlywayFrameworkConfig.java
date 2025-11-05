package io.tmgg.config;

import io.tmgg.init.SystemHook;
import io.tmgg.init.SystemHookEventType;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 框架级别的flyway手动创建执行，这样可框架使用者就不会发生冲突了
 */
@Configuration
public class FlywayFrameworkConfig implements SystemHook {


    @Bean
    public SystemHook flywayFrameworkConfig(DataSource ds) {
        return new SystemHook() {
            @Override
            public void onEvent(SystemHookEventType eventType) {
                if (eventType == SystemHookEventType.BEFORE_DATA_INIT) {
                    Flyway flyway = Flyway.configure()
                            .dataSource(ds)
                            .locations("classpath:db/migration-framework")
                            .baselineOnMigrate(true)
                            .baselineVersion("0")
                            .table("flyway_schema_history_framework")
                            .load();
                    flyway.migrate();
                }
            }
        };
    }


}
