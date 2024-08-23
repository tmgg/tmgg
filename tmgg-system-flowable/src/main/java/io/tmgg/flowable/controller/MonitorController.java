package io.tmgg.flowable.controller;


import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.FlowableLoginUserProvider;
import cn.moon.lang.BeanTool;
import cn.moon.lang.web.Page;
import cn.moon.lang.web.Pageable;
import cn.moon.lang.web.Result;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
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
  private FlowableManager fm;

  @Resource
  private TaskService taskService;

  @GetMapping("processDefinition")
  public Result processDefinition(Pageable pageable) {
    ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

    Page page = this.findAll(ProcessDefinition.class, query, pageable);

    return Result.ok().data(page);
  }

  @GetMapping("processInstance")
  public Result processInstance(Pageable pageable) {
    ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

    Page page = this.findAll(ProcessInstance.class, query, pageable);

    return Result.ok().data(page);
  }

  @GetMapping("processInstance/close")
  public Result processInstanceClose(String id) {
    String name = flowableLoginUserProvider.currentLoginUser().getName();
    runtimeService.deleteProcessInstance(id, name + "手动关闭");

    return Result.ok();
  }



  @GetMapping("task")
  public Result task(Pageable pageable) {
    TaskQuery query = taskService.createTaskQuery();

    Page page = this.findAll(TaskInfo.class, query, pageable);

    return Result.ok().data(page);
  }

  private <T extends Query<?, ?>, U extends Object> Page findAll(Class cls, Query<T, U> query, Pageable pageable) {
    long count = query.count();
    List<U> list = query.listPage((int) pageable.getOffset(), pageable.getPageSize());

    List<Map<String, Object>> mapList = BeanTool.copyToListMap(list, cls, "resources", "isNew", "currentFlowElement");


    return new Page<>(mapList, pageable, count);
  }
}
