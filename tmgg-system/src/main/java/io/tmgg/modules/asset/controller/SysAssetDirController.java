package io.tmgg.modules.asset.controller;


import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.asset.entity.SysAssetDir;
import io.tmgg.modules.asset.service.SysAssetDirService;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sysAssetDir")
public class SysAssetDirController  extends BaseController<SysAssetDir> {

    @Resource
    SysAssetDirService service;


    @PostConstruct
    public void initData(){
        SysAssetDir dir = service.findOne("DEFAULT");
        if(dir ==null){
            dir = new SysAssetDir();
            dir.setCode("DEFAULT");
            dir.setName("默认文件夹");
            service.save(dir);
        }
    }



    @GetMapping("options")
    public AjaxResult dirOptions() throws Exception {
        List<SysAssetDir> list = service.findAll(Sort.by(Sort.Direction.DESC,"updateTime"));

        List<Option> options = Option.convertList(list, BaseEntity::getId,SysAssetDir::getName);

        return AjaxResult.ok().data(options);
    }



}

