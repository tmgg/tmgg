package io.tmgg.web.persistence;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 排除了查询的基础控制器
 *
 * @param <T> 表示id为String的实体
 **/
public abstract class BaseController<T extends PersistEntity> {

    @Autowired
    private BaseService<T> service;


    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(@RequestBody Map<String, Object> param, String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<T> q = new JpaQuery<>();

        q.searchText(searchText, service.getSearchableFields());
        q.searchMap(param,service.getFields());

        Page<T> page = service.findAll(q, pageable);
        return service.autoRender(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody T input, RequestBodyKeys updateFields) throws Exception {
        service.saveOrUpdate(input, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }


    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


}
