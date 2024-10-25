package io.tmgg.flowable;

import io.tmgg.flowable.entity.SysFlowableModel;
import io.tmgg.flowable.service.MyFlowModelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.jackson.JsonTool;
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
 * 根据 接口,创建默认流程
 */
@Component
@Slf4j
public class AutoCreateFlowModelRunnerJson implements ApplicationRunner {

    @Resource
    MyFlowModelService myFlowModelService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        org.springframework.core.io.Resource[] resources = resolver.getResources("classpath*:config/flowable/*.json");


        // 遍历文件内容
        log.info("开始自动生成流程模型");
        for (org.springframework.core.io.Resource resource : resources) {
            log.info("解析流程文件 {}", resource.getFilename());
            String str = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            handleFile(str);
            log.info("解析该文件结束");
        }
    }

    private void handleFile(String json) throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        SysFlowableModel model = JsonTool.jsonToBeanQuietly(json, SysFlowableModel.class);

        model.setId(model.getCode());

        SysFlowableModel old = myFlowModelService.findByCode(model.getCode());

        if(old != null){
            model.setContent(old.getContent());
        }


        myFlowModelService.save(model);
    }


}
