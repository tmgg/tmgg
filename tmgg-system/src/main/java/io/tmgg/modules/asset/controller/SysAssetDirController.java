package io.tmgg.modules.asset.controller;


import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.asset.entity.SysAssetDir;
import io.tmgg.modules.asset.service.SysAssetDirService;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.web.pojo.param.SelectParam;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sysAssetDir")
public class SysAssetDirController  extends BaseController<SysAssetDir> {

    @Resource
    SysAssetDirService service;


    @PostConstruct
    public void initData(){
        SysAssetDir dir = service.findByCode("DEFAULT");
        if(dir ==null){
            dir = new SysAssetDir();
            dir.setCode("DEFAULT");
            dir.setName("默认文件夹");
            service.save(dir);
        }
    }



    @PostMapping("options")
    public AjaxResult dirOptions(@RequestBody SelectParam param) throws Exception {
        String searchText = param.getSearchText();
        JpaQuery<SysAssetDir> q = new JpaQuery<>();
        q.searchText(searchText, SysAssetDir.Fields.name, SysAssetDir.Fields.code);

        List<SysAssetDir> list = service.findAll(q,Sort.by(Sort.Direction.DESC,"updateTime"));

        List<Option> options = Option.convertList(list, BaseEntity::getId,SysAssetDir::getName);

        return AjaxResult.ok().data(options);
    }



}

