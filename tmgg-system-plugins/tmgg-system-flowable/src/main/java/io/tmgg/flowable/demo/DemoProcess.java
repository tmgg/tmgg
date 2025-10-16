package io.tmgg.flowable.demo;

import io.tmgg.flowable.FlowableEventType;
import io.tmgg.flowable.listener.FormKeyDescription;
import io.tmgg.flowable.listener.ProcessDefinition;
import io.tmgg.flowable.listener.ProcessDefinitionDescription;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ProcessDefinitionDescription(key = "demo",name = "demo-派车流程", formKeys = @FormKeyDescription(value = "driverForm",label = "司机表单"))
public class DemoProcess implements ProcessDefinition {

    @Override
    public void onProcessEvent(FlowableEventType type, String initiator, String businessKey, Map<String, Object> variables) {

    }
}
