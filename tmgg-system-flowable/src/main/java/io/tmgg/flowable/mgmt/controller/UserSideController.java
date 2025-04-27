package io.tmgg.flowable.mgmt.controller;


import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.bean.CommentResult;
import io.tmgg.flowable.bean.HandleTaskParam;
import io.tmgg.flowable.bean.TaskVo;
import io.tmgg.flowable.mgmt.entity.ConditionVariable;
import io.tmgg.flowable.mgmt.entity.SysFlowableModel;
import io.tmgg.flowable.FlowableLoginUser;
import io.tmgg.flowable.FlowableLoginUserProvider;
import io.tmgg.flowable.FlowableMasterDataProvider;
import io.tmgg.flowable.mgmt.service.MyFlowModelService;
import io.tmgg.flowable.mgmt.service.MyTaskService;

import io.tmgg.lang.BeanTool;
import io.tmgg.lang.DateFormatTool;
import io.tmgg.lang.ImgTool;
import io.tmgg.lang.obj.AjaxResult;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 用户侧功能，待办，处理，查看流程等
 * 每个人都可以看自己任务，故而没有权限注解
 */
@RestController
@RequestMapping("flowable/userside")
public class UserSideController {

    @Resource
    MyTaskService myTaskService;

    @Resource
    TaskService taskService;

    @Resource
    RuntimeService runtimeService;

    @Resource
    HistoryService historyService;

    @Resource
    MyFlowModelService myFlowModelService;

    @Resource
    FlowableLoginUserProvider flowableLoginUserProvider;

    @Resource
    FlowableMasterDataProvider masterDataProvider;

    @Resource
    FlowableManager fm;

    @PostMapping("todoTaskPage")
    public AjaxResult todo(Pageable pageable) {
        Page<TaskVo> page = fm.taskTodoList(pageable);
        return AjaxResult.ok().data(page);
    }

    @PostMapping("doneTaskPage")
    public AjaxResult doneTaskPage(Pageable pageable) {
        Page<TaskVo> page = fm.taskDoneList(pageable);
        return AjaxResult.ok().data(page);
    }


    // 我发起的
    @PostMapping("myInstance")
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
        Assert.state(list.size() > 0, "暂无流程信息");
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


            String formUrl = model.getFormUrl();
            if (StringUtils.isNotEmpty(formUrl)) {
                // 替换 businessKey变量
                if (instance.getBusinessKey() != null) {
                    formUrl = formUrl.replace("${businessKey}", instance.getBusinessKey());
                }
                data.put("formLink", formUrl);
            }
        }


        return AjaxResult.ok().data(data);
    }


}
