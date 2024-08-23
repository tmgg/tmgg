package io.tmgg.sys.msg.controller;

import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.msg.entity.SysMsgTopic;
import io.tmgg.sys.msg.entity.SysMsgUser;
import io.tmgg.sys.msg.result.MsgVo;
import io.tmgg.sys.msg.service.SysMsgService;
import io.tmgg.sys.msg.service.SysMsgSubscribeService;
import io.tmgg.sys.msg.service.SysMsgTopicService;
import io.tmgg.web.perm.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("sysMsgSubscribe")
public class SysMsgSubscribeController {

    @Resource
    SysMsgSubscribeService service;

    @GetMapping("myTopicList")
    public AjaxResult myList(){
        String id = SecurityUtils.getSubject().getId();
        List<String> topicList = service.findTopicListByUserId(id);

        return  AjaxResult.ok().data(topicList);
    }

    @GetMapping("subscribe")
    public AjaxResult subscribe(String topic){
        String id = SecurityUtils.getSubject().getId();

        service.subscribe(id, topic);


        return  AjaxResult.ok();
    }

    @GetMapping("unsubscribe")
    public AjaxResult unsubscribe(String topic){
        String id = SecurityUtils.getSubject().getId();
        service.unSubscribe(id, topic);
        return  AjaxResult.ok();
    }

}
