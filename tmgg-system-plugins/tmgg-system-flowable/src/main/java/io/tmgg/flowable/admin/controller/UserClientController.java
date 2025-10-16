package io.tmgg.flowable.admin.controller;


import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Pair;
import io.tmgg.flowable.FlowableLoginUser;
import io.tmgg.flowable.FlowableLoginUserProvider;
import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.FlowableMasterDataProvider;
import io.tmgg.flowable.bean.CommentResult;
import io.tmgg.flowable.bean.HandleTaskParam;
import io.tmgg.flowable.bean.TaskVo;
import io.tmgg.flowable.admin.entity.ConditionVariable;
import io.tmgg.flowable.admin.entity.SysFlowableModel;
import io.tmgg.flowable.admin.service.MyTaskService;
import io.tmgg.flowable.admin.service.SysFlowableModelService;
import io.tmgg.lang.BeanTool;
import io.tmgg.lang.DateFormatTool;
import io.tmgg.lang.ImgTool;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户侧功能，待办，处理，查看流程等
 * 每个人都可以看自己任务，故而没有权限注解
 */
@RestController
@RequestMapping("flowable/userClient")
public class UserClientController {



    @Resource
    MyTaskService myTaskService;

    @Resource
    TaskService taskService;



    @Resource
    HistoryService historyService;

    @Resource
    SysFlowableModelService myFlowModelService;

    @Resource
    FlowableLoginUserProvider flowableLoginUserProvider;

    @Resource
    FlowableMasterDataProvider masterDataProvider;

    @Resource
    FlowableManager fm;

    @RequestMapping("todoTaskPage")
    public AjaxResult todo(Pageable pageable) {
        Page<TaskVo> page = fm.taskTodoList(pageable);

        return AjaxResult.ok().data(page);
    }

    @RequestMapping("doneTaskPage")
    public AjaxResult doneTaskPage(Pageable pageable) {
        Page<TaskVo> page = fm.taskDoneList(pageable);
        return AjaxResult.ok().data(page);
    }


    // 我发起的
    @GetMapping("myInstance")
    public AjaxResult myInstance(Pageable pageable) {
        FlowableLoginUser loginUser = flowableLoginUserProvider.currentLoginUser();


        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (!loginUser.isSuperAdmin()) {
            query.startedBy(loginUser.getId());
        }


        query.orderByProcessInstanceStartTime().desc();
        query.includeProcessVariables();

        long count = query.count();

        List<HistoricProcessInstance> list = query.listPage((int) pageable.getOffset(), pageable.getPageSize());

        List<Map<String, Object>> mapList = BeanTool.copyToListMap(list, HistoricProcessInstance.class);


        for (Map<String, Object> map : mapList) {
            String startUserId = (String) map.get("startUserId");
            if (startUserId != null) {
                map.put("startUserName", masterDataProvider.getUserNameById(startUserId));
            }
        }


        return AjaxResult.ok().data(new PageImpl<>(mapList, pageable, count));
    }


    @PostMapping("handleTask")
    public AjaxResult handle(@RequestBody HandleTaskParam param) {
        FlowableLoginUser subject = flowableLoginUserProvider.currentLoginUser();
        myTaskService.handle(subject.getId(), param.getResult(), param.getTaskId(), param.getComment());
        return AjaxResult.ok().msg("处理成功");
    }

    /**
     * 任务信息
     * @param id
     * @return
     */
    @GetMapping("taskInfo")
    @Transactional
    public AjaxResult taskInfo(String id) {
        Assert.hasText(id,"任务id不能为空");
        Map<String, Object> variables = taskService.getVariables(id);
        Task task = taskService.createTaskQuery()
                .taskId(id)
                .singleResult();

        Dict data = Dict.of("id", task.getId(),
               "formKey", task.getFormKey(),
               "variables", variables
        );

        return AjaxResult.ok().data(data);
    }


    /**
     * 流程处理信息
     *
     * @return 处理流程及流程图
     */
    @GetMapping("getInstanceInfo")
    public AjaxResult instanceByBusinessKey(String businessKey, String id) throws IOException {
        Assert.state(businessKey != null || id != null, "id或businessKey不能同时为空");
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (businessKey != null) {
            query.processInstanceBusinessKey(businessKey);
        }
        if (id != null) {
            query.processInstanceId(id);
        }


        query.notDeleted();
        query.includeProcessVariables()
                .orderByProcessInstanceStartTime()
                .desc();

        List<HistoricProcessInstance> list = query
                .listPage(0, 1);
        Assert.state(!list.isEmpty(), "暂无流程信息");
        HistoricProcessInstance instance = list.get(0);


        Map<String, Object> data = BeanTool.copyToMap(HistoricProcessInstance.class, instance);

        // 处理意见
        {
            List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instance.getId());
            List<CommentResult> commentResults = processInstanceComments.stream().sorted(Comparator.comparing(Comment::getTime)).map(c -> new CommentResult(c)).collect(Collectors.toList());
            data.put("commentList", commentResults);
        }


        // 图片
        {
            BufferedImage image = myTaskService.drawImage(instance.getId());

            String base64 = ImgTool.toBase64DataUri(image);

            data.put("img", base64);
        }


        {
            String instanceName = instance.getName();
            if (instanceName == null) {
                instanceName = instance.getProcessDefinitionName();
            }
            data.put("startTime", DateFormatTool.format(instance.getStartTime()));
            data.put("starter", myTaskService.getUserName(instance.getStartUserId()));
            data.put("name", instanceName);
            data.put("id", instance.getId());


            List<Comment> processInstanceComments = taskService.getProcessInstanceComments(id);
            List<CommentResult> commentResults = processInstanceComments.stream().sorted(Comparator.comparing(Comment::getTime)).map(c -> new CommentResult(c)).collect(Collectors.toList());


            data.put("instanceCommentList", commentResults);

            SysFlowableModel model = myFlowModelService.findByCode(instance.getProcessDefinitionKey());


            List<ConditionVariable> conditionVariableList = model.getConditionVariableList();
            if (conditionVariableList != null) {
                Map<String, Object> pv = instance.getProcessVariables();
                Map<String,Object> variables = new HashMap<>();
                for (ConditionVariable con : conditionVariableList) {
                    String name = con.getName();
                    String label = con.getLabel();
                    variables.put(label, pv.get(name));
                }
                data.put("variables",variables);
            }
        }


        return AjaxResult.ok().data(data);
    }


}
