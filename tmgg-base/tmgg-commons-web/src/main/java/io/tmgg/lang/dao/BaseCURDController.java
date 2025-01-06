package io.tmgg.lang.dao;

import io.tmgg.lang.dao.specification.JpaQuery;
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
 *
 * 已弃用，为了保证稳定性，请直接将增删查改代码放到业务Controller中
 **/
@Deprecated
public abstract class BaseCURDController<T extends Persistable<String>> {

    @Autowired
    private BaseService<T> service;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page(T param, String keyword, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<T> q = new JpaQuery<>();
        q.likeExample(param);

//        q.searchText(keyword, "name", "phone");

        Page<T> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody T param) throws Exception {
        T result = service.saveOrUpdate(param);
        return AjaxResult.ok().data( result.getId()).msg("保存成功");
    }



    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }




}
