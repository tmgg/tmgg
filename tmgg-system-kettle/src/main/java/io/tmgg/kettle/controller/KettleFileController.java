package io.tmgg.kettle.controller;

import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.Result;
import io.github.tmgg.kettle.sdk.plugin.RepTreeItem;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.XmlTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("kettle/file")
public class KettleFileController {


    @Resource
    KettleSdk sdk;

    @RequestMapping("upload")
    public AjaxResult uploadFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String xml = new String(bytes, StandardCharsets.UTF_8);

        Result result = sdk.uploadRepositoryObject(xml);

        if (!result.isSuccess()) {
            return AjaxResult.err().msg(result.getMessage());
        }

        return AjaxResult.ok();
    }

    @RequestMapping("list")
    public AjaxResult listFile() {
        try {
            List<RepTreeItem> list = sdk.getRepositoryObjectTree();

            List<MyTreeItem> itemList = list.stream().map(i -> {
                MyTreeItem myTreeItem = new MyTreeItem();
                myTreeItem.setId(i.getId());
                myTreeItem.setPid(i.getPid());
                myTreeItem.setName(i.getName());
                myTreeItem.setModifiedDate(i.getModifiedDate());

                return myTreeItem;
            }).collect(Collectors.toList());

            TreeManager<MyTreeItem> tm = new TreeManager<>(itemList, MyTreeItem::getId, MyTreeItem::getPid, MyTreeItem::getChildren, MyTreeItem::setChildren);

            List<MyTreeItem> tree = tm.getTree();

            return AjaxResult.ok().data(tree);
        } catch (Exception e) {
            return AjaxResult.err().msg(e.getMessage());
        }
    }

    @RequestMapping("delete")
    public AjaxResult deleteFile(String id) {
        Result result = sdk.deleteRepositoryObject(id);

        if (!result.isSuccess()) {
            return AjaxResult.err().msg(result.getMessage());
        }

        return AjaxResult.ok();
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<RepTreeItem> list = sdk.getRepositoryObjectTree();

        List<Option> options = new ArrayList<>();

        for (RepTreeItem f : list) {
            if (!f.getId().endsWith(".kjb")) {
                continue;
            }
            String name = f.getName();
            options.add(new Option(name, f.getId()));
        }

        return AjaxResult.ok().data(options);
    }

    @Getter
    @Setter
    public static class MyTreeItem extends RepTreeItem {
        List<MyTreeItem> children = new ArrayList<>();
    }


}
