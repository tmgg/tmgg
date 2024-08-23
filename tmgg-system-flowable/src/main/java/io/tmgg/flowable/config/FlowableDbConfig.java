package io.tmgg.flowable.config;


import cn.tmgg.dbtool.DbTool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class FlowableDbConfig {

    @Primary
    @Bean
    @ConditionalOnMissingBean(value = DbTool.class)
    public DbTool dbTool(DataSource dataSource){
        return new DbTool(dataSource);
    }
}
