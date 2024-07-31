package io.tmgg.lang.dao;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 简单的增删查改
 **/
public abstract class BaseCURDController<T extends Persistable<String>> {

    @Autowired
    private BaseService<T> service;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page(T param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        Page<T> page = service.findByExampleLike(param, pageable);
        return AjaxResult.success(null, page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody T param) throws Exception {
        T result = service.saveOrUpdate(param);
        return AjaxResult.success("保存成功", result.getId());
    }


    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(@RequestBody T param) {
        service.deleteById(param.getId());
        return AjaxResult.success("删除成功");
    }

    @HasPermission
    @GetMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.success("删除成功");
    }


    @GetMapping("get")
    public AjaxResult get(String id) {
        return AjaxResult.success(null, service.findOne(id));
    }


}
