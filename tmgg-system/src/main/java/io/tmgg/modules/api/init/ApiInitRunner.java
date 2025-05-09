package io.tmgg.modules.api.init;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.modules.api.Action;
import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.api.entity.ApiResourceArgument;
import io.tmgg.modules.api.entity.ApiResourceArgumentReturn;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.service.ApiResourceService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Component
public class ApiInitRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        init();
    }


    private void init() {
        Map<String, Object> beans = SpringTool.getBeansOfType(Object.class);
        String[] basePackageNames = SpringTool.getBasePackageNames();
        beans.forEach((beanName, bean)->{
            String pkg = bean.getClass().getPackageName();
            if (StrUtil.startWithAny(pkg, basePackageNames)) {
                saveOne(beanName,bean);
            }

        });

    }

    private void saveOne(String beanName, Object bean) {
        Method[] methods = bean.getClass().getMethods();

        for (Method method : methods) {
            Action api = method.getAnnotation(Action.class);
            if (api == null) {
                return;
            }

            ApiResource r = new ApiResource();
            r.setId(api.uri());
            r.setName(api.name());
            r.setUri(api.uri());
            r.setDesc(api.desc());
            r.setBeanName(beanName);

            r.setBean(bean);
            r.setMethod(method);

            r.setReturnType(method.getReturnType().getSimpleName());
            r.setParameterList(parseArgs(method));
            r.setReturnList(parseReturnArgs(method));

            service.add(r);
        }
    }



    @NotNull
    private List<ApiResourceArgument> parseArgs(Method method) {

        Class<?>[] parameters = method.getParameterTypes();
        StandardReflectionParameterNameDiscoverer u = new StandardReflectionParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<ApiResourceArgument> parameterList = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            String type = parameters[i].getSimpleName();
            String name = paramNames[i];

            ApiResourceArgument a = new ApiResourceArgument();
            a.setName(name);
            a.setType(type);
            a.setIndex(i);


            Annotation[] anns = parameterAnnotations[i];
            if (anns.length > 0) {
                Annotation ann = anns[0];
                if (ann instanceof FieldInfo f) {
                    a.setRequired(f.required());
                    a.setDesc(f.label());
                    a.setDemo(f.demo());
                    if(f.len() > 0){
                        a.setLen(f.len());
                    }
                }
            }

            parameterList.add(a);
        }
        return parameterList;
    }


    public List<ApiResourceArgumentReturn> parseReturnArgs(Method method){
        Class<?> returnType = method.getReturnType();


        boolean isSimpleType = returnType.isPrimitive() || String.class.isAssignableFrom(returnType);
        if (isSimpleType) {
            return null;
        }

        boolean isCollection = Collection.class.isAssignableFrom(returnType);
        if (isCollection) {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();// 泛型
            Type type = parameterizedType.getActualTypeArguments()[0];

          return getClassFields((Class<?>) type);
        }
        return getClassFields(returnType);
    }


    private List<ApiResourceArgumentReturn> getClassFields(Class<?> cls) {
        List<ApiResourceArgumentReturn> list = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
            if (jsonIgnore != null) {
                continue;
            }

            ApiResourceArgumentReturn dict = new ApiResourceArgumentReturn();
            dict.setName( field.getName());
            dict.setType(field.getType().getSimpleName());

            FieldInfo f = field.getAnnotation(FieldInfo.class);
            if (f != null) {
                dict.setRequired(f.required());
                dict.setDesc( f.label());
                dict.setDemo( f.demo());
            } else {
                Msg msg = field.getAnnotation(Msg.class);
                if (msg != null) {
                    dict.setDesc(msg.value());
                }
            }

            list.add(dict);
        }
        return list;
    }


    @Resource
    private ApiResourceService service;
}
