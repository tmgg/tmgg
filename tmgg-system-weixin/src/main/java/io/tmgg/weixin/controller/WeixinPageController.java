package io.tmgg.weixin.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.weixin.entity.WeixinPage;
import io.tmgg.weixin.service.WeixinPageService;
import jakarta.annotation.Resource;
import lombok.Data;
import org.jsoup.Jsoup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    private JpaQuery<WeixinPage> buildQuery(CommonQueryParam param) {
        JpaQuery<WeixinPage> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), WeixinPage.Fields.title,WeixinPage.Fields.appId,WeixinPage.Fields.path, WeixinPage.Fields.root);
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<WeixinPage> q = buildQuery(param);

        Page<WeixinPage> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

    @Data
    public static class ImportPagesParam {
        String appId;
        String content;
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
            if(StrUtil.isEmpty(weixinPage.getTitle())){
                weixinPage.setTitle(weixinPage.getPath());
            }
        }

        service.saveAll(list);


        return AjaxResult.ok();
    }

    private void parsePages(Map<String,Object> map, List<WeixinPage> list){
        String root = (String) map.getOrDefault("root","root");
        List<Map<String,Object>> pages = (List<Map<String, Object>>) map.get("pages");
        for (Map<String, Object> page : pages) {
            String path = (String) page.get("path");
            Map<String, Object> style = (Map<String, Object>) page.get("style");
            String title = (String) style.get("navigationBarTitleText");

            WeixinPage item = new WeixinPage();
            item.setRoot(root);
            item.setPath(path);
            item.setTitle(title);
            list.add(item);
        }

    }


    @PostMapping("options")
    public AjaxResult options() throws Exception {
        List<WeixinPage> page = service.findAll(Sort.by(WeixinPage.Fields.path));
        Option.convertList(page, WeixinPage::getPath, WeixinPage::getTitle);
        return AjaxResult.ok().data(page);
    }

}

