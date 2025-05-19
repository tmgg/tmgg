package io.tmgg.flowable;

import io.tmgg.flowable.bean.TaskVo;
import io.tmgg.flowable.mgmt.entity.ConditionVariable;
import io.tmgg.flowable.mgmt.entity.SysFlowableModel;
import io.tmgg.flowable.mgmt.service.MyFlowModelService;
import io.tmgg.flowable.mgmt.service.MyTaskService;

import io.tmgg.lang.DateFormatTool;
import lombok.Getter;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FlowableManager {

    // 变量KEY
    public static final String VAR_USER_ID = "userId";
    public static final String VAR_USER_NAME = "userName";
    public static final String VAR_UNIT_ID = "unitId";
    public static final String VAR_UNIT_NAME = "unitName";
    public static final String VAR_DEPT_ID = "deptId";
    public static final String VAR_DEPT_NAME = "deptName";


    @Getter
    @Resource
    private ProcessEngine engine;

    @Resource
    private MyFlowModelService modelService;

    @Resource
    private FlowableLoginUserProvider flowableLoginUserProvider;

    @Resource
    private MyTaskService myTaskService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private HistoryService historyService;


    public void start(String processDefinitionKey, String bizKey, Map<String, Object> variables) {
        start(processDefinitionKey, bizKey, null, variables);
    }

    public void start(String processDefinitionKey, String bizKey, String title, Map<String, Object> variables) {
        RepositoryService repositoryService = getEngine().getRepositoryService();
        long count = repositoryService.createDeploymentQuery().deploymentKey(processDefinitionKey).count();
        Assert.state(count > 0, "流程未配置，请联系管理配置流程" + processDefinitionKey);

        RuntimeService runtimeService = getEngine().getRuntimeService();
        long instanceCount = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bizKey).active().count();
        Assert.state(instanceCount == 0, "流程审批中，请勿重复提交");

        if (variables == null) {
            variables = new HashMap<>();
        }

        // 判断必填流程变量
        {
            SysFlowableModel model = modelService.findByCode(processDefinitionKey);
            List<ConditionVariable> conditionVariable = model.getConditionVariableList();
            if (!CollectionUtils.isEmpty(conditionVariable)) {
                for (ConditionVariable formItem : conditionVariable) {
                    String name = formItem.getName();
                    Assert.state(variables.containsKey(name), "流程异常, 必填变量未设置：" + formItem.getLabel() + ":" + name);
                    Object v = variables.get(name);
                    Assert.notNull(v, "流程异常, 必填变量未设置：" + formItem.getLabel() + ":" + name);
                }
            }
        }

        FlowableLoginUser loginUser = flowableLoginUserProvider.currentLoginUser();


        // 添加一些发起人的相关信息
        String startUserId = loginUser.getId();
        Assert.hasText(startUserId, "当前登录人员ID不能为空");
        variables.put(VAR_USER_ID, startUserId);
        variables.put(VAR_USER_NAME, loginUser.getName());

        variables.put(VAR_UNIT_ID, loginUser.getUnitId());
        variables.put(VAR_UNIT_NAME, loginUser.getUnitName());
        variables.put(VAR_DEPT_ID, loginUser.getDeptId());
        variables.put(VAR_DEPT_NAME, loginUser.getDeptName());
        variables.put("BUSINESS_KEY", bizKey);

        // 流程名称
        ProcessDefinition def = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey).active()
                .latestVersion()
                .singleResult();

        if (title == null) {
            String day = DateFormatTool.formatDayCn(new Date());
            title = loginUser.getName() + day + "发起的【" + def.getName() + "】";
        }

        // 设置发起人, 该方法会自动设置流程变量 INITIATOR -> startUserId
        getEngine().getIdentityService().setAuthenticatedUserId(startUserId);


        // 启动
        runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(processDefinitionKey)
                .businessKey(bizKey)
                .variables(variables)
                .name(title)
                .start();
    }


    public Page<TaskVo> taskTodoList(Pageable pageable) {
        FlowableLoginUser me = flowableLoginUserProvider.currentLoginUser();
        TaskQuery taskQuery = myTaskService.createTodoTaskQuery(me);
        taskQuery.orderByTaskCreateTime().desc();

        List<Task> taskList = taskQuery.listPage((int) pageable.getOffset(), pageable.getPageSize());
        long count = taskQuery.count();


        List<TaskVo> infoList = taskList.stream().map(task -> {
            ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

            TaskVo taskVo = new TaskVo(task);
            taskVo.fillInstanceInfo(instance);
            return taskVo;
        }).collect(Collectors.toList());

        return new PageImpl<>(infoList, pageable, count);
    }

    public long taskTodoCount() {
        FlowableLoginUser me = flowableLoginUserProvider.currentLoginUser();
        TaskQuery taskQuery = myTaskService.createTodoTaskQuery(me);
        long count = taskQuery.count();
        return count;
    }


    public Page<TaskVo> taskDoneList(Pageable pageable) {
        FlowableLoginUser me = flowableLoginUserProvider.currentLoginUser();


        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery().finished();

        // 为方便调试，超级管理员可以查看所有
        if (!me.isSuperAdmin()) {
            taskQuery.taskAssigneeIds(Arrays.asList(me.getId()));
        }


        //根据任务创建时间倒序，最新任务在最上面
        taskQuery.orderByHistoricTaskInstanceEndTime().desc();

        taskQuery.includeProcessVariables();

        List<HistoricTaskInstance> taskList = taskQuery.listPage((int) pageable.getOffset(), pageable.getPageSize());
        long count = taskQuery.count();


        List<TaskVo> infoList = taskList.stream().map(task -> {

            HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

            TaskVo taskVo = new TaskVo(task);
            taskVo.fillInstanceInfo(instance);
            return taskVo;
        }).collect(Collectors.toList());

        return new PageImpl<>(infoList, pageable, count);
    }




    public String taskFormUrl(TaskInfo task) {
        String url = "/#/flowable/task/todoTaskForm?taskId=" + task.getId() + "&instanceId=" + task.getProcessInstanceId();
        return url;
    }


}
