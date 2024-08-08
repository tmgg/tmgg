package io.tmgg.kettle.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.Result;
import io.github.tmgg.kettle.sdk.plugin.RepTreeItem;
import io.github.tmgg.kettle.sdk.response.SlaveServerJobStatus;
import io.github.tmgg.kettle.sdk.response.SlaveServerStatus;
import io.tmgg.kettle.entity.KettleFile;
import io.tmgg.kettle.service.KettleFileService;
import io.tmgg.lang.JsonTool;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.XmlTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.TreeOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("kettle/file")
public class KettleFileController {

    @Resource
    KettleFileService kettleFileService;

    @Resource
    KettleSdk sdk;

    @RequestMapping("upload")
    public AjaxResult uploadFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String xml = new String(bytes, StandardCharsets.UTF_8);

        Result result = sdk.uploadRepObject(xml);

        if(!result.isSuccess()){
            return AjaxResult.err().msg(result.getMessage());
        }

        return AjaxResult.ok();
    }

    @RequestMapping("list")
    public AjaxResult listFile() {
        List<RepTreeItem> list = sdk.getRepObjects();

        List<MyTreeItem> itemList = list.stream().map(i -> {
            MyTreeItem myTreeItem = new MyTreeItem();
            myTreeItem.setId(i.getId());
            myTreeItem.setPid(i.getPid());
            myTreeItem.setName(i.getName());
            myTreeItem.setModifiedDate(i.getModifiedDate());

            return myTreeItem;
        }).collect(Collectors.toList());

        TreeManager<MyTreeItem> tm = new TreeManager<>(itemList,MyTreeItem::getId,MyTreeItem::getPid,MyTreeItem::getChildren, MyTreeItem::setChildren);

        List<MyTreeItem> tree = tm.getTree();


        return AjaxResult.ok().data(tree);
    }

    @RequestMapping("delete")
    public AjaxResult deleteFile(String id) {
        Result result = sdk.deleteRepObject(id);

        if(!result.isSuccess()){
            return AjaxResult.err().msg(result.getMessage());
        }

        return AjaxResult.ok();
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<RepTreeItem> list = sdk.getRepObjects();

        List<Option> options = new ArrayList<>();

        for (RepTreeItem f : list) {
            if(!f.getId().endsWith(".kjb")){
                continue;
            }


       /*     List<Dict> data = new ArrayList<>();
            List<KettleFile.Parameter> parameterList = f.getParameterList();
            if (parameterList != null) {
                data = parameterList.stream().map(p -> {
                    Dict d = new Dict();
                    d.put("key", p.getName());
                    d.put("value", p.getDefaultValue());
                    return d;
                }).collect(Collectors.toList());
            }
*/

            String name = f.getName();
            options.add(new Option(name, name));
        }

        return AjaxResult.ok().data(options);
    }

    @Getter
    @Setter
    public static class MyTreeItem extends RepTreeItem {
        List<MyTreeItem> children = new ArrayList<>();
    }
}
