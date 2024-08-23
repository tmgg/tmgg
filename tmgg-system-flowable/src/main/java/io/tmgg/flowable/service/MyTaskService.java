package io.tmgg.flowable.service;

import io.tmgg.flowable.FlowableProperties;
import io.tmgg.flowable.assignment.AssignmentService;
import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.bean.TaskHandleResult;
import io.tmgg.flowable.FlowableLoginUser;
import io.tmgg.flowable.FlowableMasterDataProvider;
import io.tmgg.lang.SpringTool;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程流转
 */
@Slf4j
@Component
public class MyTaskService {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private AssignmentService assignmentService;

    @Resource
    private FlowableMasterDataProvider flowableMasterDataProvider;

    @Resource
    FlowableProperties flowableProperties;

    public TaskQuery createTodoTaskQuery(FlowableLoginUser loginUser) {
        String loginUserId = loginUser.getId();


        TaskQuery query = taskService.createTaskQuery().active();

        if (loginUser.isSuperAdmin()) {
            return query;
        }


        Collection<AssignmentTypeProvider> providerList = SpringTool.getBeans(AssignmentTypeProvider.class);

        {
            // 人员及 分组
            Set<String> groupIds = new HashSet<>();
            for (AssignmentTypeProvider provider : providerList) {
                List<String> groups = provider.findGroupsByUser(loginUserId);
                if (groups != null) {
                    groupIds.addAll(groups);
                }
            }

            query.or();
            query.taskAssignee(loginUserId);
            query.taskCandidateUser(loginUserId);
            if (!CollectionUtils.isEmpty(groupIds)) {
                query.taskCandidateGroupIn(groupIds);
            }
            query.endOr();
        }

        query.orderByTaskCreateTime().desc();

        return query;
    }


    public synchronized boolean isBizKeyExist(String bizKey) {
        long count = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bizKey).count();
        return count > 0;
    }


    public void handle(String userId, TaskHandleResult result, String taskId, String comment) {
        Assert.notNull(userId, "用户Id不能为空");
        //校验任务是否存在
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Assert.state(task != null, "任务已经处理过，请勿重复操作");

        //获取流程实例id
        String processInstanceId = task.getProcessInstanceId();
        comment = task.getName() + "：" + result.getMessage() + "。" + comment;
        addComment(processInstanceId, taskId, userId, comment);

        String assignee = task.getAssignee();
        // if (StringUtils.hasText(assignee)) {
        //设置办理人为当前用户
        taskService.setAssignee(taskId, userId);
        //   }


        if (result == TaskHandleResult.APPROVE) {
            taskService.complete(taskId);
            return;
        }

        // 点击拒绝（不同意）
        if (result == TaskHandleResult.REJECT) {
            switch (flowableProperties.getRejectType()) {
                case DELETE:
                    closeAndDelete(comment, task);
                    break;
                case MOVE_BACK:
                    this.moveBack(task);

                    break;
            }

            return;
        }
    }

    private void closeAndDelete(String comment, Task task) {
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), comment);
    }

    // 回退上一个节点
    private void moveBack(Task task) {
        log.debug("开始回退任务 {}", task);
        List<UserTask> userTaskList = myBpmnModelService.findPreActivity(task);
        for (UserTask userTask : userTaskList) {
            log.debug("回退任务 {}", userTask);
        }

        List<String> ids = userTaskList.stream().map(t -> t.getId()).collect(Collectors.toList());

        if (ids.isEmpty()) {
            this.closeAndDelete("回退节点为空，终止流程", task);
            return;
        }



        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveSingleExecutionToActivityIds(task.getExecutionId(), ids)
                .changeState();


    }


    public String getUserName(String userId) {
        if (userId == null) {
            return null;
        }
        return flowableMasterDataProvider.getUserNameById(userId);
    }


    public String getAssigneeInfoByTaskId(TaskInfo task) {
        if (StringUtils.hasText(task.getAssignee())) {
            return getUserName(task.getAssignee());
        }

        // 对于已办 由于处理任务设置了Assignee， 以下代码几乎不会调用到（为了兼容老代办）
        String executionId = task.getExecutionId();

        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery()
                .executionId(executionId)
                .processDefinitionId(task.getProcessDefinitionId())
                .activityType("userTask")
                .processInstanceId(task.getProcessInstanceId());

        // 没有taskId 的选项， 只能返回list后再过滤
        List<HistoricActivityInstance> historicActivityInstanceList = historicActivityInstanceQuery.list();
        if (historicActivityInstanceList == null || historicActivityInstanceList.isEmpty()) {
            // 流程历史可能已删除
            return null;
        }

        HistoricActivityInstance historicActivityInstance = null;
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
            if (activityInstance.getTaskId().equals(task.getId())) {
                historicActivityInstance = activityInstance;
                break;
            }
        }
        if (historicActivityInstance == null) {
            return null;
        }

        String activityId = historicActivityInstance.getActivityId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getFlowElement(activityId);

        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;

            Map<String, List<ExtensionAttribute>> attr = userTask.getAttributes();
            List<ExtensionAttribute> type = attr.get("assignmentType");
            List<ExtensionAttribute> obj = attr.get("assignmentObject");

            if (!CollectionUtils.isEmpty(type) && !CollectionUtils.isEmpty(obj)) {
                return assignmentService.getObjectName(type.get(0).getValue(), obj.get(0).getValue());
            }

        }

        return null;
    }


    private void addComment(String processInstanceId, String taskId, String taskAssignee, String comment) {
        Comment addComment = taskService.addComment(taskId, processInstanceId, comment);
        addComment.setUserId(taskAssignee);
        taskService.saveComment(addComment);
    }


    /**
     * @deprecated 替换MyBpmnModelService
     * @param instanceId
     * @return
     */
    public BufferedImage drawImage(String instanceId) {
      return myBpmnModelService.drawImage(instanceId);
    }


    @Resource
    MyBpmnModelService myBpmnModelService;
}
