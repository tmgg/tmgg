package io.tmgg.weixin.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.pojo.param.SelectParam;
import io.tmgg.weixin.entity.WeixinPage;
import io.tmgg.weixin.service.WeixinPageService;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("weixinPage")
public class WeixinPageController extends BaseController<WeixinPage> {

    @Resource
    private  WeixinPageService service;






    @Data
    public static class ImportPagesParam {
        String appId;
        String content;
    }
    @HasPermission(label = "清空")
    @GetMapping({"clean"})
    public AjaxResult clean(){
        service.deleteAll();
        return AjaxResult.ok();
    }


    @HasPermission(label = "导入页面")
    @PostMapping({"importPages"})
    public AjaxResult importPages(@RequestBody ImportPagesParam param) throws IOException {
        String content = param.getContent();
        String appId = param.getAppId();

        // 去掉注释
        List<String> lines = StrUtil.split(content, "\n");
        content = lines.stream().filter(t -> !t.trim().startsWith("//")).collect(Collectors.joining("\n"));


        Map<String, Object> map = JsonTool.jsonToMap(content);

        ArrayList<WeixinPage> list = new ArrayList<>();
        parsePages(map, list);

        List<Map<String,Object>> subPackages = (List<Map<String, Object>>) map.get("subPackages");
        for (Map<String, Object> subMap : subPackages) {
            parsePages(subMap, list);
        }

        for (WeixinPage weixinPage : list) {

            weixinPage.setAppId(appId);
        }


        service.saveAll(list);


        return AjaxResult.ok();
    }

    private void parsePages(Map<String,Object> map, List<WeixinPage> list){
        String root = (String) map.getOrDefault("root", null);
        List<Map<String,Object>> pages = (List<Map<String, Object>>) map.get("pages");
        for (Map<String, Object> pageData : pages) {
            String path = (String) pageData.get("path");
            Map<String, Object> style = (Map<String, Object>) pageData.get("style");
            String title = (String) style.get("navigationBarTitleText");

            if(StrUtil.isBlank(title)){
                continue;
            }

            String page = "/";
            if (root != null){
                page += root + "/";
            }
            page += path;



            WeixinPage item = new WeixinPage();
            item.setRoot(root);
            item.setPath(path);
            item.setTitle(title);
            item.setPage(page);
            list.add(item);
        }

    }


    @PostMapping("options")
    public AjaxResult options(@RequestBody SelectParam param) {
        String searchText = param.getSearchText();
        JpaQuery<WeixinPage> q = new JpaQuery<>();
        q.searchText(searchText, WeixinPage.Fields.page, WeixinPage.Fields.title);
        List<WeixinPage> list = service.findAll(Sort.by(WeixinPage.Fields.root,WeixinPage.Fields.page));
        List<Option> options = Option.convertList(list, WeixinPage::getPage, t-> {
            String title = t.getTitle();

            return title + " " + t.getPage();
        });

        // 去重
        options = options.stream().distinct().collect(Collectors.toList());

        return AjaxResult.ok().data(options);
    }

}

