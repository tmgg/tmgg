package io.tmgg.flowable.listener;

import io.tmgg.flowable.FlowableEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 流程定义接口
 */
public interface FlowableListener {



    /**
     *
     * @param type
     * @param initiator 发起人
     * @param businessKey 业务标识，如key
     * @param variables 变量
     */
    @Transactional
    void onProcessEvent(FlowableEventType type, String initiator, String businessKey, Map<String, Object> variables);


}
