package io.tmgg.flowable.controller;



import io.tmgg.flowable.bean.TaskVo;
import io.tmgg.flowable.service.MyTaskService;
import cn.moon.lang.web.Result;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("flowable/instance")
public class InstanceController {


    @Resource
    MyTaskService myTaskService;

    @Resource
    HistoryService historyService;

    @Resource
    private TaskService taskService;

    @Resource
    RuntimeService runtimeService;

    @GetMapping("img")
    public void instanceImg(String businessKey, String id, HttpServletResponse response) throws IOException {
        if (StringUtils.isNotEmpty(businessKey)) {
            HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
            query.processInstanceBusinessKey(businessKey);
            query.notDeleted().orderByProcessInstanceStartTime()
                    .desc();
            List<HistoricProcessInstance> list = query
                    .listPage(0, 1);
            Assert.state(list.size() > 0, "暂无流程信息");
            HistoricProcessInstance instance = list.get(0);

            id = instance.getId();
        }

        BufferedImage image = myTaskService.drawImage(id);
        ImageIO.write(image, "jpg", response.getOutputStream());
    }

    @GetMapping("todoList")
    public Result taskList(String businessKey, String id) {
        if (StringUtils.isNotEmpty(businessKey)) {
            HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
            query.processInstanceBusinessKey(businessKey);
            query.notDeleted().orderByProcessInstanceStartTime()
                    .desc();
            List<HistoricProcessInstance> list = query
                    .listPage(0, 1);
            Assert.state(list.size() > 0, "暂无流程信息");
            HistoricProcessInstance instance = list.get(0);

            id = instance.getId();
        }


        TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(id).orderByTaskCreateTime().desc();

        List<Task> taskList = taskQuery.list();


        List<TaskVo> infoList = taskList.stream().map(task -> {

            TaskVo taskVo = new TaskVo(task);
            return taskVo;
        }).collect(Collectors.toList());

        return Result.ok().data(infoList);
    }



}
