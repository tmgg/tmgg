package io.tmgg.modules.api.init;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.Remark;
import io.tmgg.modules.api.ApiMapping;
import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.api.defaults.DefaultApi;
import io.tmgg.modules.api.entity.ApiResourceArgument;
import io.tmgg.modules.api.entity.ApiResourceArgumentReturn;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.service.ApiResourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/***
 *
 * 开放接口
 * @gendoc
 * @see DefaultApi
 */
@Component
@Slf4j
public class ApiInit implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 清理接口（使用中的会有关联关系）
        List<ApiResource> list = service.findAll();
        for (ApiResource apiResource : list) {
            try {
                service.delete(apiResource);
            }catch (Exception e){
                // 尝试删除失败
                log.trace("接口 {}含有关联关系 {}", apiResource.getAction(), e.getMessage());
            }
        }

        log.info("解析接口...");
        init();
    }


    private void init() {
        Map<String, Object> beans = SpringTool.getBeansOfType(Object.class);
        String[] basePackageNames = SpringTool.getBasePackageNames();
        beans.forEach((beanName, bean)->{
            String pkg = bean.getClass().getPackageName();
            if (StrUtil.startWithAny(pkg, basePackageNames)) {
                service.saveOrUpdate(beanName,bean);
            }
        });
    }



    @Resource
    private ApiResourceService service;
}
