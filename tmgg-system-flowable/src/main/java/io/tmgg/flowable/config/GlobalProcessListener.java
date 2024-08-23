package io.tmgg.flowable.config;

import io.tmgg.flowable.FlowableProcessListener;
import cn.moon.lang.web.SpringTool;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.HistoryService;
import org.flowable.engine.delegate.event.impl.FlowableProcessEventImpl;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.PROCESS_CANCELLED;
import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.PROCESS_COMPLETED;

@Slf4j
@Component
public class GlobalProcessListener implements FlowableEventListener {


    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        if (!(flowableEvent instanceof FlowableProcessEventImpl)) {
            return;
        }

        String name = flowableEvent.getType().name();
        if (!name.startsWith("PROCESS")) {
            return;
        }

        FlowableEngineEventType eventType = FlowableEngineEventType.valueOf(name);
        boolean allow = eventType == PROCESS_CANCELLED || eventType == PROCESS_COMPLETED;

        if (!allow) {
            return;
        }

        FlowableProcessEventImpl event = (FlowableProcessEventImpl) flowableEvent;
        String instanceId = event.getProcessInstanceId();

        ExecutionEntityImpl execution = (ExecutionEntityImpl) event.getExecution();
        String definitionKey = execution.getProcessDefinitionKey();


        FlowableProcessListener listener = findListener(definitionKey);
        if (listener == null) {
            return;
        }


        log.info("流程事件 {} {}", definitionKey, event.getType());

        Map<String, Object> variables = execution.getVariables();
        String businessKey = (String) variables.get("BUSINESS_KEY");
        String initiator = (String) variables.get("INITIATOR");

        // 兼容性代码
        if(businessKey == null){
            HistoryService historyService = SpringTool.getBean(HistoryService.class);

            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();

            if (historicProcessInstance == null) {
                return;
            }

             businessKey = historicProcessInstance.getBusinessKey();
        }



        // 触发
        listener.onProcessEvent(eventType, initiator, businessKey, variables);
    }




    private FlowableProcessListener findListener(String key) {
        Collection<FlowableProcessListener> values = SpringTool.getBeansOfType(FlowableProcessListener.class).values();
        if(values.isEmpty()){
            return null;
        }
        return values.stream().filter(d -> key.equals(d.getProcessDefinitionKey())).findFirst().orElse(null);
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }


}
