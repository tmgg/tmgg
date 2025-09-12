package io.tmgg.flowable;

import lombok.AllArgsConstructor;

// 参考 FlowableEngineEventType， 名称保持一致
@AllArgsConstructor
public enum FlowableEventType {

    TASK_ASSIGNED("任务分配"),
    TASK_COMPLETED("任务完成"),

    /**
     * 流程创建和启动通常一起的
     */
    PROCESS_CREATED("流程创建"),

    PROCESS_STARTED("流程启动"),

    PROCESS_COMPLETED("流程完成"),

    PROCESS_CANCELLED("流程终止");


    final String msg;


}
