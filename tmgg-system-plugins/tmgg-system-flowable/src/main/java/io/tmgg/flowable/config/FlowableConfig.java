
package io.tmgg.flowable.config;

import io.tmgg.flowable.listener.FlowableListener;
import io.tmgg.flowable.listener.FlowableListenerRegister;
import io.tmgg.flowable.listener.FlowableListenerRegistry;
import io.tmgg.lang.IdTool;
import jakarta.annotation.Resource;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Resource
    private GlobalProcessListener globalProcessListener;

    @Resource
    private List<FlowableListener> listeners;

    @Resource
    @Lazy
    private  FlowableListenerRegistry flowableListenerRegistry;

    @Override
    public void configure(SpringProcessEngineConfiguration cfg) {
        // 主键生成器，注意：不会影响act_de开头的表主键生成，因为这是流程设计器的，不是工作流引擎的
        cfg.setIdGenerator(IdTool::uuidV7);


        if (cfg.getEventListeners() == null) {
            cfg.setEventListeners(new ArrayList<>());
        }
        cfg.getEventListeners().add(globalProcessListener);


        for (FlowableListener listener : listeners) {
            FlowableListenerRegister register = listener.getClass().getAnnotation(FlowableListenerRegister.class);
            Assert.notNull(register, "监听器必须使用注解" + FlowableListenerRegistry.class.getSimpleName() + "描述");
            flowableListenerRegistry.addListener(register.processDefinitionKey(), listener);
        }
    }

}
