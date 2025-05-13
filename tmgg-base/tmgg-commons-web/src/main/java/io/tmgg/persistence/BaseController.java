package io.tmgg.persistence;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 排除了查询的基础控制器
 * @param <T> 表示id为String的实体
 *
 **/
public abstract class BaseController<T extends PersistEntity> {

    @Autowired
    private BaseService<T> service;


//    @HasPermission
//    @RequestMapping("page")
//    public AjaxResult page(@RequestBody T param, String keyword, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
//        JpaQuery<T> q = new JpaQuery<>();
//        q.likeExample(param);
//
////        q.searchText(keyword, "name", "phone");
//
//        Page<T> page = service.findAll(q, pageable);
//        return AjaxResult.ok().data(page);
//    }

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
