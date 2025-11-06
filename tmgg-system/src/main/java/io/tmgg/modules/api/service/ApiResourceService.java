package io.tmgg.modules.api.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.field.FieldDescription;
import io.tmgg.modules.api.ApiMapping;
import io.tmgg.modules.api.dao.ApiResourceDao;
import io.tmgg.modules.api.entity.ApiResource;
import io.tmgg.modules.api.entity.ApiResourceArgument;
import io.tmgg.modules.api.entity.ApiResourceArgumentReturn;
import io.tmgg.data.service.BaseService;
import jakarta.annotation.Resource;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Service
public class ApiResourceService extends BaseService<ApiResource> {

    @Resource
    private ApiResourceDao dao;

    @Resource
    private ApiAccountResourceService accountResourceService;


    private final Map<String, Method> pathBindings = new HashMap<>();

    public Method findMethodByAction(String action) {
        return pathBindings.get(action);
    }

    public List<ApiResource> findAll() {
        return dao.findAll(Sort.by(ApiResource.Fields.action));
    }


    @Transactional
    public void add(ApiResource r) {
        ApiResource old = dao.findByName(r.getName());
        if (old != null) {
            if (old.getAction() == null || !old.getAction().equals(r.getAction())) {
                accountResourceService.deleteByResource(old);
                dao.delete(old);
                dao.flush();
            }
        }


        dao.save(r);
        pathBindings.put(r.getAction(), r.getMethod());
    }


    public List<ApiResource> removeNotExist(List<ApiResource> list) {
        return list.stream().filter(t -> pathBindings.containsKey(t.getAction())).toList();
    }

    @Transactional
    public void saveOrUpdate(String beanName, Object bean) {
        Method[] methods = bean.getClass().getMethods();

        for (Method method : methods) {
            ApiMapping api = method.getAnnotation(ApiMapping.class);
            if (api == null) {
                return;
            }

            ApiResource r = new ApiResource();
            String action = api.action();
            Assert.state(!action.contains("/"), "action不能包含斜杠: " + action);
            r.setId(action);
            r.setName(api.name());
            r.setAction(action);
            r.setDesc(api.desc());
            r.setBeanName(beanName);

            r.setBean(bean);
            r.setMethod(method);

            r.setReturnType(method.getReturnType().getSimpleName());
            r.setParameterList(parseArgs(method));
            r.setReturnList(parseReturnArgs(method));

            this.add(r);
        }
    }

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
                if (ann instanceof FieldDescription f) {
                    a.setRequired(f.required());
                    a.setDesc(f.label());
                    a.setDemo(f.demo());
                    if (f.len() > 0) {
                        a.setLen(f.len());
                    }
                }
            }

            parameterList.add(a);
        }
        return parameterList;
    }


    public List<ApiResourceArgumentReturn> parseReturnArgs(Method method) {
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
            dict.setName(field.getName());
            dict.setType(field.getType().getSimpleName());

            FieldDescription f = field.getAnnotation(FieldDescription.class);
            if (f != null) {
                dict.setRequired(f.required());
                dict.setDesc(f.label());
                dict.setDemo(f.demo());
            } else {
                Remark msg = field.getAnnotation(Remark.class);
                if (msg != null) {
                    dict.setDesc(msg.value());
                }
            }

            list.add(dict);
        }
        return list;
    }

    public ApiResource findAction(String action) {
        return dao.findByAction(action);
    }
}
