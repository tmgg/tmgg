package io.tmgg.code.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import io.tmgg.code.bean.BeanInfo;
import io.tmgg.code.service.CodeGenService;
import io.tmgg.lang.FreemarkerTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("code/gen")
public class SysCodeGenController {

    public static final String[] TEMPLATES = {"list", "treeForm"};

    public static final String TEMPLATE_FOLDER = "code-gen-template";

    @Resource
    CodeGenService codeGenService;

    @GetMapping("templateOptions")
    public AjaxResult templateOptions() throws Exception {
        List<Option> options = Option.convertList(TEMPLATES);

        return AjaxResult.ok().data(options);
    }

    @GetMapping("genDetail")
    public AjaxResult genDetail(String id, String template) throws Exception {
        Map<String, String> map = getGenContent(id, template);

        List<Map<String,String>> list = new ArrayList<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            // antd 的属性
            HashMap<String, String> item = new HashMap<>();
            item.put("key",key);
            item.put("value",key);
            item.put("label",FileUtil.getName(key));
            item.put("children",value);


            list.add(item);
        }


        return AjaxResult.ok().data(list);
    }

    private Map<String, String> getGenContent(String id, String template) throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        genCode(id, TEMPLATE_FOLDER + "/common", map);
        genCode(id, TEMPLATE_FOLDER + "/" + template, map);
        return map;
    }


    @PostMapping
    public AjaxResult gen(@RequestBody GenParam param) throws Exception {
        String id = param.getId();

        Map<String, String> map = getGenContent(id, param.getTemplate());


        for (Map.Entry<String, String> e : map.entrySet()) {
            String file = e.getKey();
            String content = e.getValue();
            FileUtil.writeUtf8String(content, file);
        }
        return AjaxResult.ok().msg("生成成功");
    }


    /**
     * @param templateFolder
     * @return map: {file: content}
     * @throws Exception
     */
    private Map<String, String> genCode(String cls, String templateFolder, Map<String, String> resultMap) throws Exception {
        Properties prop = getConfig(templateFolder);


        BeanInfo bean = codeGenService.getBeanInfo(Class.forName(cls));
        Map<String, Object> model = BeanUtil.beanToMap(bean);

        for (Map.Entry<Object, Object> e : prop.entrySet()) {
            String templateFile = (String) e.getKey();
            String targetFile = (String) e.getValue();
            targetFile = FreemarkerTool.renderString(targetFile, model);

            String template = ResourceUtil.readUtf8Str(templateFolder + "/" + templateFile);

            log.info("准备渲染文件：{}={}", templateFile, targetFile);
            String result = FreemarkerTool.renderString(template, model);
            log.info("渲染结果\n {}", result);

            resultMap.put(targetFile, result);
        }

        return resultMap;
    }

    private static Properties getConfig(String templateFolder) throws IOException {
        Properties prop = new Properties();
        readConfig(templateFolder, prop);
        return prop;
    }

    private static void readConfig(String templateFolder, Properties prop) throws IOException {
        InputStream configStream = ResourceUtil.getStream(templateFolder + "/config.properties");
        prop.load(configStream);
        configStream.close();
    }


    @Data
    public static class GenParam {
        String id;
        String template;
    }


}
