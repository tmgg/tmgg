
package io.tmgg.sys.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.sys.entity.SysDict;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.sys.service.SysDictDataService;
import io.tmgg.sys.service.SysDictService;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("sysDict")
public class SysDictController {

    @Resource
    private SysDictService typeService;

    @Resource
    private SysDictDataService dataService;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page() {
        List<SysDict> page = typeService.findAll(Sort.by(Sort.Direction.DESC, SysDict.FIELD_UPDATE_TIME));
        return AjaxResult.ok().data(page);
    }




    /**
     * 获取字典类型下所有字典，举例，返回格式为：[{code:"M",value:"男"},{code:"F",value:"女"}]
     * <p>
     * yubaoshan
     */
    @GetMapping("dataDict")
    public Dict dataDict(String code) {
        SysDict type = typeService.findOne(new JpaQuery().eq(SysDict.Fields.code, code));

        List<SysDictItem> list = dataService.findAll(new JpaQuery().eq(SysDictItem.Fields.typeId, type.getId()));

        Dict dict = new Dict();
        for (SysDictItem data : list) {
            dict.put(data.getCode(), data.getValue());
        }

        return dict;
    }

    /**
     * 获取字典类型下所有字典，举例，返回格式为：[{code:"M",value:"男"},{code:"F",value:"女"}]
     */
    @GetMapping("dict")
    public Dict dict(String searchValue) {
        JpaQuery<SysDict> query = new JpaQuery<>();

        if (searchValue != null)
            query.like("name", searchValue);

        List<SysDict> list = typeService.findAll();

        Dict dict = new Dict();
        for (SysDict data : list) {
            dict.put(data.getCode(), data.getName());
        }

        return dict;
    }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysDict sysDictParam) {

        typeService.save(sysDictParam);
        return AjaxResult.ok();
    }


    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(@RequestBody SysDict sysDictParam) {
        typeService.delete(sysDictParam);
        return AjaxResult.ok();
    }


    /**
     * 系统字典类型与字典值构造的树
     * 登录刷新时调用
     */
    @GetMapping("tree")
    @PublicApi
    public AjaxResult tree() {
        return AjaxResult.ok().data(typeService.tree());
    }

    @GetMapping("options")
    public AjaxResult options() {
        List<SysDict> list = typeService.findAll();
        return AjaxResult.ok().data(Option.convertList(list, SysDict::getCode, SysDict::getName));
    }

}
