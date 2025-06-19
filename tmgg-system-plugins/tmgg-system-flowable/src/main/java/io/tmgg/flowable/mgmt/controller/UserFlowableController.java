package io.tmgg.flowable.mgmt.controller;


import io.tmgg.flowable.FlowableManager;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
