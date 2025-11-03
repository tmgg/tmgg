package ${modulePackageName}.controller;

import io.tmgg.data.query.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
{modulePackageName}.entity.${name};
import ${modulePackageName}.service.${name}Service;


import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${firstLowerName}")
public class ${name}Controller  {

    @Resource
    private ${name}Service service;

    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(${name} ${firstLowerName}, String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<${name}> q = new JpaQuery<>();
        q.searchText(searchText, service.getSearchableFields());
        q.searchParams(${firstLowerName}, service.getDomainClass());

        Page<${name}> page = service.findAllByClient(q, pageable);

        return AjaxResult.ok().data(page);
   }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody ${name} input, RequestBodyKeys updateFields) throws Exception {
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

