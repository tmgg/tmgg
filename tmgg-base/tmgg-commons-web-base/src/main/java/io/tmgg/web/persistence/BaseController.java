package io.tmgg.web.persistence;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.WebConstants;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.persistence.specification.JpaQuery;
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
 **/
public abstract class BaseController<T extends PersistEntity> {

    @Autowired
    private BaseService<T> service;


    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(   @RequestParam Map<String, Object> param, String searchText,
            @RequestHeader(value = WebConstants.HEADER_EXPORT_TYPE, required = false) String exportType, // 导出标志
            @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<T> q = new JpaQuery<>();
        q.searchText(searchText, service.getSearchableFields());

        // 移除分页参数后再查询
        param.remove("size");
        param.remove("page");
        q.searchParams(param, service.getDomainClass());

        Page<T> page = service.findAllByClient(q, pageable);

        if ("EXCEL".equals(exportType)) {
            service.exportExcel(page, service.getDomainClass());
            return null;
        }

        return AjaxResult.ok().data(page);
    }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody T input, RequestBodyKeys updateFields) throws Exception {
        service.saveOrUpdateByClient(input, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }


    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteByClient(id);
        return AjaxResult.ok().msg("删除成功");
    }


}
