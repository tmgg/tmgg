package io.tmgg.modules.sys.msg.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.msg.entity.SysMsgTopic;
import io.tmgg.modules.sys.msg.entity.SysMsgUser;
import io.tmgg.modules.sys.msg.service.SysMsgSubscribeService;
import io.tmgg.modules.sys.msg.service.SysMsgTopicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("sysMsgTopic")
public class SysMsgTopicController {


    @Resource
    SysMsgTopicService sysMsgTopicService;

    @GetMapping("list")
    public AjaxResult list(){
        List<SysMsgTopic> list = sysMsgTopicService.findAll();

        return AjaxResult.ok().data(list);

    }

}
