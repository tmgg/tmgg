package io.tmgg.sys.msg.controller;

import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.msg.entity.SysMsgUser;
import io.tmgg.sys.msg.result.MsgVo;
import io.tmgg.sys.msg.service.SysMsgService;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.sql.SQLException;

@RestController
@RequestMapping("msg")
public class SysMsgController {

    @Resource
    SysMsgService service;


    @Data
    public static class QueryParam{
        Boolean read;
    }

    @RequestMapping("page")
    public AjaxResult page(@RequestBody QueryParam queryParam,
                           @PageableDefault(sort = BaseEntity.FIELD_CREATE_TIME, direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        Page<MsgVo> page = service.page(pageable, SecurityUtils.getSubject().getId(), queryParam.getRead());

        return AjaxResult.ok().data(page);
    }

    @PostMapping("read")
    public AjaxResult read(@RequestBody SysMsgUser msg) {
        service.read(msg.getId());

        return AjaxResult.ok();
    }


}
