package io.tmgg.modules.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sys.job")
@Data
public class JobProp {


    /**
     * 定时任务，全局开关 , 某些情况如开发时，可按需关闭
     */
    private boolean enable = true;



}
