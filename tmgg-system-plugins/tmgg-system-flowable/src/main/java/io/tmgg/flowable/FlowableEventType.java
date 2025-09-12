package io.tmgg.flowable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FlowableEventType {


    PROCESS_COMPLETED("完成"),

    PROCESS_CANCELLED("取消");


    final String msg;


}
