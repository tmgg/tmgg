package io.tmgg.flowable.controller;


import io.tmgg.flowable.FlowableLoginUser;
import io.tmgg.flowable.FlowableLoginUserProvider;
import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.FlowableMasterDataProvider;
import io.tmgg.flowable.bean.CommentResult;
import io.tmgg.flowable.bean.HandleTaskParam;
import io.tmgg.flowable.bean.TaskVo;
import io.tmgg.flowable.entity.ConditionVariable;
import io.tmgg.flowable.entity.SysFlowableModel;
import io.tmgg.flowable.service.MyFlowModelService;
import io.tmgg.flowable.service.MyTaskService;
import io.tmgg.lang.BeanTool;
import io.tmgg.lang.DateFormatTool;
import io.tmgg.lang.ImgTool;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
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
@RequestMapping("user/flowable")
public class UserFlowableController {



    @Resource
    FlowableManager fm;

    @GetMapping("messageCount")
    public AjaxResult todo() {
        long count = fm.taskTodoCount();
        return AjaxResult.ok().data(count);
    }


}
