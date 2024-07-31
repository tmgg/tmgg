
package io.tmgg.sys.dict.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.sys.dict.entity.SysDictData;
import io.tmgg.sys.dict.entity.SysDictType;
import io.tmgg.sys.dict.service.SysDictDataService;
import io.tmgg.sys.dict.service.SysDictTypeService;
import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统字典类型
 */
@RestController
@RequestMapping("sysDictType")
public class SysDictTypeController {

    @Resource
    private SysDictTypeService typeService;

    @Resource
    private SysDictDataService dataService;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page() {
        List<SysDictType> page = typeService.findAll(Sort.by(Sort.Direction.DESC, SysDictType.FIELD_UPDATE_TIME));
        return AjaxResult.success(page);
    }


    /**
     * 获取字典类型下所有字典，举例，返回格式为：[{code:"M",value:"男"},{code:"F",value:"女"}]
     */
    @GetMapping("dropDown")
    public AjaxResult dropDown(SysDictType sysDictTypeParam) {
        return AjaxResult.success(typeService.dropDown(sysDictTypeParam));
    }

    /**
     * 获取字典类型下所有字典，举例，返回格式为：[{code:"M",value:"男"},{code:"F",value:"女"}]
     * <p>
     * yubaoshan
     */
    @GetMapping("dataDict")
    @BusinessLog("系统字典类型数据_下拉")
    public Dict dataDict(String code) {
        SysDictType type = typeService.findOne(new JpaQuery().eq(SysDictType.Fields.code, code));

        List<SysDictData> list = dataService.findAll(new JpaQuery().eq(SysDictData.Fields.typeId, type.getId()));

        Dict dict = new Dict();
        for (SysDictData data : list) {
            dict.put(data.getCode(), data.getValue());
        }

        return dict;
    }

    /**
     * 获取字典类型下所有字典，举例，返回格式为：[{code:"M",value:"男"},{code:"F",value:"女"}]
     */
    @GetMapping("dict")
    @BusinessLog("系统字典类型_下拉")
    public Dict dict(String searchValue) {
        JpaQuery<SysDictType> query = new JpaQuery<>();

        if (searchValue != null)
            query.like("name", searchValue);

        List<SysDictType> list = typeService.findAll();

        Dict dict = new Dict();
        for (SysDictType data : list) {
            dict.put(data.getCode(), data.getName());
        }

        return dict;
    }


    @HasPermission
    @PostMapping("save")
    @BusinessLog("系统字典类型_增加")
    public AjaxResult save(@RequestBody SysDictType sysDictTypeParam) {

        typeService.save(sysDictTypeParam);
        return AjaxResult.success();
    }


    @HasPermission
    @PostMapping("delete")
    @BusinessLog("系统字典类型_删除")
    public AjaxResult delete(@RequestBody SysDictType sysDictTypeParam) {
        typeService.delete(sysDictTypeParam);
        return AjaxResult.success();
    }


    /**
     * 系统字典类型与字典值构造的树
     * 登录刷新时调用
     */
    @GetMapping("tree")
    @PublicApi
    public AjaxResult tree() {
        return AjaxResult.success(typeService.tree());
    }

    @GetMapping("options")
    public AjaxResult options() {
        List<SysDictType> list = typeService.findAll();
        return AjaxResult.success(Option.convertList(list, SysDictType::getCode, SysDictType::getName));
    }

}
