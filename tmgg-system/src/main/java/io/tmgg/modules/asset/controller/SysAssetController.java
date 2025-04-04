package io.tmgg.modules.asset.controller;

import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.modules.asset.entity.SysAsset;
import io.tmgg.modules.asset.entity.SysAssetType;
import io.tmgg.modules.asset.service.SysAssetService;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.web.CommonQueryParam;


import io.tmgg.web.annotion.HasPermission;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("sysAsset")
public class SysAssetController  extends BaseController<SysAsset>{

    @Resource
    SysAssetService service;


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  CommonQueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = {"type","name"}) Pageable pageable)
            throws Exception {
        JpaQuery<SysAsset> q = new JpaQuery<>();
        String pid = (String) param.getData().get("pid");
        q.eq(SysAsset.Fields.pid, pid);


        // 关键字搜索，请补全字段
        q.searchText(param.getKeyword(), SysAsset.Fields.name);

        Page<SysAsset> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

    @HasPermission(label = "编辑内容")
    @PostMapping("saveContent")
    public AjaxResult saveContent(@RequestBody SysAsset param) throws Exception {
         service.saveContent(param);
        return AjaxResult.ok().msg("保存成功");
    }
    @GetMapping("dirOptions")
    public AjaxResult dirOptions() throws Exception {
        List<SysAsset> list = service.findAll(SysAssetType.DIR);

        List<Option> options = Option.convertList(list, SysAsset::getId, SysAsset::getName);

        return AjaxResult.ok().data(options);
    }

    @GetMapping("tree")
    public AjaxResult tree() throws Exception {
        List<SysAsset> list = service.findAll(SysAssetType.DIR);

        List<TreeOption> options = TreeOption.convertTree(list, SysAsset::getId, SysAsset::getPid, SysAsset::getName);

        return AjaxResult.ok().data(options);
    }

    @PublicRequest
    @GetMapping({"preview/{code}" ,"preview/{code}.*" }) // 可以加后缀，如 /sysAsset/preview/bg.png
    public void preview(@PathVariable String code, HttpServletResponse resp) throws Exception {
        service.preview(code, resp);
    }
}

