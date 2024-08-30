package io.tmgg.weapp.admin;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.weapp.entity.Weapp;
import io.tmgg.weapp.service.WeappService;
import io.tmgg.web.annotion.HasPerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("weapp")
public class WeappController {

    @Resource
    private WeappService service;


    @HasPerm
    @GetMapping("page")
    public AjaxResult page(Weapp param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        Page<Weapp> page = service.findByExampleLike(param, pageable);
        return AjaxResult.ok().data( page);
    }

    @HasPerm
    @PostMapping("save")
    public AjaxResult save(@RequestBody Weapp param) throws Exception {
        Weapp result = service.saveOrUpdate(param);
        return AjaxResult.ok().msg("保存成功").data( result.getId());
    }



    @HasPerm
    @GetMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }

}
