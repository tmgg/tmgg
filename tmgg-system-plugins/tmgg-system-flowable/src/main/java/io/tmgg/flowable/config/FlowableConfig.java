
package io.tmgg.flowable.config;

import cn.hutool.core.annotation.AnnotationUtil;
import io.tmgg.flowable.listener.FlowableListener;
import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.listener.FlowableListenerDesc;
import io.tmgg.flowable.listener.FlowableListenerRegistry;
import io.tmgg.lang.IdTool;
import jakarta.annotation.Resource;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration>, CommandLineRunner {

    @Resource
    private GlobalProcessListener globalProcessListener;

    @Resource
    private List<FlowableListener> listeners;

    @Resource
    @Lazy
    FlowableListenerRegistry flowableListenerRegistry;

    @Override
    public void configure(SpringProcessEngineConfiguration cfg) {
        // 主键生成器，注意：不会影响act_de开头的表主键生成，因为这是流程设计器的，不是工作流引擎的
        cfg.setIdGenerator(IdTool::uuidV7);


        if (cfg.getEventListeners() == null) {
            cfg.setEventListeners(new ArrayList<>());
        }
        cfg.getEventListeners().add(globalProcessListener);
    }


    @Override
    public void run(String... args) throws Exception {
        for (FlowableListener listener : listeners) {
            FlowableListenerDesc desc = listener.getClass().getAnnotation(FlowableListenerDesc.class);
            if(desc != null){
                flowableListenerRegistry.addListener(desc.processDefinitionKey(),listener);
            }
        }
    }
}
