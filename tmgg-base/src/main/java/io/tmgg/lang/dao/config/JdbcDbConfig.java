package io.tmgg.lang.dao.config;


import io.tmgg.lang.jdbc.Jdbc;
import cn.moon.dbtool.DbTool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class JdbcDbConfig {

    @Primary
    @Bean
    @ConditionalOnMissingBean(value = Jdbc.class)
    public Jdbc jdbc(DataSource dataSource){
        return new Jdbc(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(value = DbTool.class)
    public DbTool dbTool(DataSource dataSource){
        return new Jdbc(dataSource);
    }
}
