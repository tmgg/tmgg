
package io.tmgg.flowable.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.UUID;


@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Resource
    GlobalProcessListener globalProcessListener;

    @Override
    public void configure(SpringProcessEngineConfiguration cfg) {
        // 主键生成器，注意：不会影响act_de开头的表主键生成，因为这是流程设计器的，不是工作流引擎的
        cfg.setIdGenerator(() -> UUID.randomUUID().toString().replace("-", ""));


        if(cfg.getEventListeners() == null){
            cfg.setEventListeners(new ArrayList<>());
        }
        cfg.getEventListeners().add(globalProcessListener);
    }
}
