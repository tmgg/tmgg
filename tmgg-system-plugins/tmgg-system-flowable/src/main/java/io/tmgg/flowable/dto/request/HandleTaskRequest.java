package io.tmgg.flowable.dto.request;

import io.tmgg.flowable.dto.TaskHandleResult;
import lombok.Data;

@Data
public class HandleTaskRequest {

    TaskHandleResult result;
    String taskId;
    String comment;
}
