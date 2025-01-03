package io.tmgg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "sys.job")
@Data
public class JobProp {

    /**
     * 定时任务，全局开关 , 某些情况如开发时，可按需关闭
     */
    private boolean enable = true;

    /**
     * job日志是否透传。设置为false只存数据库
     */
    private boolean loggerAdditive = true;

    /**
     * job日志名称，匹配到的logger打印的日志会存数据库
     */
    private String loggerName = "job";

}
