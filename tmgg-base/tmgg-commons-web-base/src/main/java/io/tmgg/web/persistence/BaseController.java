package io.tmgg.web.persistence;

import io.tmgg.data.domain.PersistEntity;
import io.tmgg.data.service.BaseService;
import io.tmgg.dto.AjaxResult;
import io.tmgg.web.WebConstants;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.data.query.JpaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 排除了查询的基础控制器
 *
 * @param <T> 表示id为String的实体
 *
 * @deprecated 无法处理复杂情况下的参数解析
 **/
@Deprecated
public abstract class BaseController<T extends PersistEntity> {

    @Autowired
    private BaseService<T> service;


    @Deprecated
    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(   @RequestParam Map<String, Object> param, String searchText,
            @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<T> q = new JpaQuery<>();
        q.searchText(searchText, service.getSearchableFields());

        // 移除分页参数后再查询
        param.remove("size");
        param.remove("page");
        q.searchParams(param, service.getDomainClass());

        Page<T> page = service.findAllByRequest(q, pageable);



        return AjaxResult.ok().data(page);
    }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody T input, RequestBodyKeys updateFields) throws Exception {
        service.saveOrUpdateByRequest(input, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }


    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteByRequest(id);
        return AjaxResult.ok().msg("删除成功");
    }


}
