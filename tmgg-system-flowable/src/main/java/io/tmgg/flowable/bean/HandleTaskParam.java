package io.tmgg.flowable.bean;

import lombok.Data;

@Data
public class HandleTaskParam {

    TaskHandleResult result;
    String taskId;
    String comment;
}
