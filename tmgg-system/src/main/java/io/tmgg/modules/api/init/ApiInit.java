package io.tmgg.modules.api.init;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.api.defaults.MathApi;
import io.tmgg.modules.api.entity.ApiAccountResource;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import io.tmgg.modules.api.service.ApiResourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

/***
 *
 * 开放接口
 * @gendoc
 * @see MathApi
 */
@Component
@Slf4j
public class ApiInit implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("解析接口...");
        init();
    }


    private void init() {
        Map<String, Object> beans = SpringTool.getBeansOfType(Object.class);
        String[] basePackageNames = SpringTool.getBasePackageNames();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            String pkg = bean.getClass().getPackageName();
            if (StrUtil.startWithAny(pkg, basePackageNames)) {
                service.saveOrUpdate(beanName, bean);
            }
        }
    }



    @Resource
    private ApiResourceService service;

    @Resource
    private ApiAccountResourceService accountResourceService;
}
