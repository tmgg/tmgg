package io.tmgg.weapp.admin;

import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.weapp.entity.WeappUser;
import io.tmgg.weapp.service.WeappUserService;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weappUser")
public class WeappUserController  {

    @Resource
    private WeappUserService service;


    @HasPermission(value = "weapp:userList", title = "用户列表")
    @GetMapping("page")
    public AjaxResult page(WeappUser param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        Page<WeappUser> page = service.findByExampleLike(param, pageable);
        return AjaxResult.success(null, page);
    }
}