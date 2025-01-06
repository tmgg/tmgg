package io.tmgg.flowable.controller;


import io.tmgg.flowable.FlowableLoginUserProvider;
import io.tmgg.lang.BeanTool;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
import org.flowable.common.engine.api.query.Query;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 流程监控
 */
@RequestMapping("flowable/monitor")
@RestController
public class MonitorController {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private FlowableLoginUserProvider flowableLoginUserProvider;


    @Resource
    private TaskService taskService;

    @PostMapping("processDefinition")
    public AjaxResult processDefinition(Pageable pageable) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

        Page page = this.findAll(ProcessDefinition.class, query, pageable);

        return AjaxResult.ok().data(page);
    }

    @PostMapping("processInstance")
    public AjaxResult processInstance(Pageable pageable) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

        Page page = this.findAll(ProcessInstance.class, query, pageable);

        return AjaxResult.ok().data(page);
    }

    @PostMapping("processInstance/close")
    public AjaxResult processInstanceClose(String id) {
        String name = flowableLoginUserProvider.currentLoginUser().getName();
        runtimeService.deleteProcessInstance(id, name + "手动关闭");

        return AjaxResult.ok();
    }


    @PostMapping("task")
    public AjaxResult task(Pageable pageable) {
        TaskQuery query = taskService.createTaskQuery();

        Page page = this.findAll(TaskInfo.class, query, pageable);

        return AjaxResult.ok().data(page);
    }

    private <T extends Query<?, ?>, U extends Object> Page findAll(Class cls, Query<T, U> query, Pageable pageable) {
        long count = query.count();
        List<U> list = query.listPage((int) pageable.getOffset(), pageable.getPageSize());

        List<Map<String, Object>> mapList = BeanTool.copyToListMap(list, cls, "resources", "isNew", "currentFlowElement");

        return new PageImpl<>(mapList, pageable, count);
    }
}
