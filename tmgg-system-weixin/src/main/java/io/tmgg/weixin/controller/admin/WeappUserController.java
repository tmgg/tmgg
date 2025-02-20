package io.tmgg.weixin.controller.admin;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.weixin.entity.WeixinUser;
import io.tmgg.weixin.service.WeappUserService;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weappUser")
public class WeappUserController  {

    @Resource
    private WeappUserService service;


    @HasPermission("weapp:userList")
    @RequestMapping("page")
    public AjaxResult page(@RequestBody WeixinUser param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        Page<WeixinUser> page = service.findByExampleLike(param, pageable);
        return AjaxResult.ok().data( page);
    }
}
