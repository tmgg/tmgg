package io.tmgg.modules.sys.controller;


import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysUserMessage;
import io.tmgg.modules.sys.service.SysUserMessageService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("user/msg")
public class UserMsgController {


    @Resource
    SysUserMessageService sysUserMsgService;


    @RequestMapping("page")
    public AjaxResult page(Boolean read, @PageableDefault(sort = BaseEntity.FIELD_CREATE_TIME, direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        String id = SecurityUtils.getSubject().getId();
        Page<SysUserMessage> page = sysUserMsgService.findByUser(id, read, pageable);
        return AjaxResult.ok().data(page);
    }

    @PostMapping("read")
    public AjaxResult read(@RequestBody SysUserMessage msg) {
        sysUserMsgService.read(msg.getId());

        return AjaxResult.ok();
    }

    @GetMapping("getMessageCount")
    public AjaxResult getMessageCount() {
        String id = SecurityUtils.getSubject().getId();
        long sum = sysUserMsgService.countUnReadByUser(id);

        return AjaxResult.ok().data(sum);
    }

}
