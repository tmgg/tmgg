package io.tmgg.persistence.config;


import io.tmgg.dbtool.DbTool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbcDbConfig {



    @Bean
    @ConditionalOnMissingBean(value = DbTool.class)
    public DbTool dbTool(DataSource dataSource){
        return new DbTool(dataSource);
    }
}
