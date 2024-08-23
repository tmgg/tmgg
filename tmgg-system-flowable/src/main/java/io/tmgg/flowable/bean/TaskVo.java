package io.tmgg.flowable.bean;

import io.tmgg.flowable.service.MyTaskService;
import io.tmgg.lang.SpringTool;
import lombok.Data;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.Date;


@Data
public class TaskVo {
    String id;

    String instanceId;
    String instanceName;
    Date instanceStartTime;
    String instanceStarter;

    String instanceStatusLabel;

    Date createTime;
    Date endTime;
    String taskName;
    String assigneeInfo;
    float durationInHours;


    public TaskVo(TaskInfo task) {
        id = task.getId();
        taskName = task.getName();
        createTime = task.getCreateTime();
        assigneeInfo = SpringTool.getBean(MyTaskService.class).getAssigneeInfoByTaskId(task);

        if (task instanceof HistoricTaskInstance ) {
            HistoricTaskInstance hisTask =  ((HistoricTaskInstance) task);
            endTime = hisTask.getEndTime();

            Long durationInMillis = hisTask.getDurationInMillis();
            durationInHours = durationInMillis / (60 * 60.0F * 1000);
        }
    }

    public void fillInstanceInfo(ProcessInstance instance) {
        instanceId = instance.getProcessInstanceId();
        instanceName = instance.getName();
        if (instanceName == null) {
            instanceName = instance.getProcessDefinitionName();
        }
        instanceStartTime =  ((instance.getStartTime()));
        instanceStarter = SpringTool.getBean(MyTaskService.class).getUserName(instance.getStartUserId());
    }

    public void fillInstanceInfo(HistoricProcessInstance instance) {
        instanceId = instance.getId();
        instanceStartTime = instance.getStartTime();
        instanceStarter = SpringTool.getBean(MyTaskService.class).getUserName(instance.getStartUserId());


        instanceStatusLabel = instance.getEndTime() == null ? "进行中" : "已结束";

        instanceName = instance.getName();
        if (instanceName == null) {
            instanceName = instance.getProcessDefinitionName();
        }
    }



}
