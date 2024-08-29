package io.tmgg.code.controller;

import io.tmgg.code.service.CodeGenService;
import io.tmgg.code.bean.BeanInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import io.tmgg.lang.FreemarkerTool;
import io.tmgg.lang.obj.AjaxResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@RestController
@RequestMapping("code/gen")
public class SysCodeGenController {

    @Resource
    CodeGenService codeGenService;


    @PostMapping
    public AjaxResult gen(@RequestBody GenParam param) throws Exception {
        List<String> ids = param.getIds();

        Map<String, String> map = genMap(ids, "code-gen-template/common");
        Map<String, String> map2 = genMap(ids, "code-gen-template/" + param.getTemplate());
        map.putAll(map2);


        if (param.getGenType() == GenType.project) {
            writeToProject(map);
            return AjaxResult.ok().msg("生成成功");
        }
        if (param.getGenType() == GenType.zip) {


            return AjaxResult.ok().msg("生成成功");
        }


        return AjaxResult.err();
    }

    private void writeToProject(Map<String,String> map) throws Exception {
        for (Map.Entry<String, String> e : map.entrySet()) {
            String file = e.getKey();
            String content = e.getValue();
            FileUtil.writeUtf8String(content, file);
        }
    }




    /**
     * @param ids
     * @param templateFolder
     * @return map: {file: content}
     * @throws Exception
     */
    private Map<String, String> genMap(List<String> ids, String templateFolder) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        InputStream configStream = ResourceUtil.getStream(templateFolder + "/config.properties");
        Properties prop = new Properties();
        prop.load(configStream);
        configStream.close();


        for (String cls : ids) {
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
        }

        return resultMap;
    }


    @Data
    public static class GenParam {
        List<String> ids;
        GenType genType; //
        String template;
    }

    public enum GenType {
        project,
        zip
    }
}
