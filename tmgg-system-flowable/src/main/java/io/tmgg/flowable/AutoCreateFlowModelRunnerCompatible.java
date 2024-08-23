package io.tmgg.flowable;

import io.tmgg.flowable.entity.FlowModel;
import io.tmgg.flowable.service.MyFlowModelService;
import cn.moon.lang.json.XmlTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

/**
 *  为了兼容老程序写的
 */
@Component
@Slf4j
public class AutoCreateFlowModelRunnerCompatible implements ApplicationRunner {

    @Resource
    MyFlowModelService myFlowModelService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        org.springframework.core.io.Resource[] resources = resolver.getResources("classpath*:config/flowable/*.xml");


        // 遍历文件内容
        log.info("开始自动生成流程模型");
        for (org.springframework.core.io.Resource resource : resources) {
            log.info("解析流程文件 {}", resource.getFilename());
            String xml = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            handleFile(xml);
        }
    }

    private void handleFile(String xml) throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        FlowModel model = XmlTool.xmlToBean(xml, FlowModel.class);

        model.setId(model.getCode());

        FlowModel old = myFlowModelService.findByCode(model.getCode());

        if(old != null){
            model.setContent(old.getContent());
        }


        myFlowModelService.save(model);
    }


}
