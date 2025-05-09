package io.tmgg.modules.openapi.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.Build;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.openapi.ApiResource;
import io.tmgg.modules.openapi.OpenApi;
import io.tmgg.modules.openapi.OpenApiField;
import io.tmgg.modules.openapi.entity.OpenApiAccount;
import io.tmgg.modules.openapi.service.ApiAccountService;
import io.tmgg.modules.openapi.service.ApiResourceService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("openApiAccount")
public class ApiAccountController extends BaseController<OpenApiAccount> {

    @Resource
    private ApiAccountService service;

    @Resource
    private ApiResourceService apiResourceService;


    private JpaQuery<OpenApiAccount> buildQuery(CommonQueryParam param) {
        JpaQuery<OpenApiAccount> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), "name", OpenApiAccount.Fields.accessIp, OpenApiAccount.Fields.perms);
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<OpenApiAccount> q = buildQuery(param);

        Page<OpenApiAccount> page = service.findAll(q, pageable);

        Map<String, String> permMap = apiResourceService.findAll().stream().collect(Collectors.toMap(t -> t.getOpenApi().action(), t -> t.getOpenApi().name()));
        for (OpenApiAccount a : page) {
            List<String> perms = a.getPerms();
            String permsLabel = perms.stream().map(p -> permMap.get(p)).collect(Collectors.joining(","));
            a.getExtData().put("permsLabel", permsLabel);
        }


        return AjaxResult.ok().data(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody OpenApiAccount param) throws Exception {
        if (param.isNew()) {
            param.setAppSecret(RandomUtil.randomString(32));
        }

        OpenApiAccount result = service.saveOrUpdate(param);
        return AjaxResult.ok().data(result.getId()).msg("保存成功");
    }


    @GetMapping("resourceOptions")
    public AjaxResult resourceOptions() {
        Collection<ApiResource> all = apiResourceService.findAll();

        List<Option> options = all.stream().map(r -> {
            OpenApi openApi = r.getOpenApi();
            return Option.builder().label(openApi.name()).value(openApi.action()).build();
        }).collect(Collectors.toList());

        return AjaxResult.ok().data(options);
    }


    @GetMapping("docInfo")
    public AjaxResult docInfo(String id) {
        OpenApiAccount acc = service.findOne(id);
        Collection<ApiResource> list = apiResourceService.findAll();
        List<ApiResource> apis = list.stream()
                .filter(r -> acc.getPerms().contains(r.getOpenApi().action()))
                .sorted((o1, o2) -> {
                    String action1 = o1.getOpenApi().action();
                    String action2 = o2.getOpenApi().action();
                    return action1.compareTo(action2);
                }).toList();

        List<Map<String, Object>> apiInfoList = apis.stream().map(this::getApiInfo).toList();

        Dict resultData = new Dict();
        resultData.put("apiList", apiInfoList);
        resultData.put("frameworkVersion", Build.FRAMEWORK_VERSION);

        return AjaxResult.ok().data(resultData);
    }

    @NotNull
    private Map<String, Object> getApiInfo(ApiResource r) {
        OpenApi api = r.getOpenApi();
        Map<String, Object> info = new HashMap<>();

        info.put("action", api.action());
        info.put("name", api.name());
        info.put("desc", api.desc());

        Method method = r.getMethod();

        Class<?>[] parameters = method.getParameterTypes();
        StandardReflectionParameterNameDiscoverer u = new StandardReflectionParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<Dict> parameterList = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            String type = parameters[i].getSimpleName();
            String name = paramNames[i];

            Dict dict = Dict.of("name", name, "type", type);

            Annotation[] anns = parameterAnnotations[i];
            if (anns.length > 0) {
                Annotation ann = anns[0];
                if (ann instanceof OpenApiField f) {
                    dict.put("required", f.required());
                    dict.put("desc", f.desc());
                    dict.put("demo", f.demo());
                }
            }

            parameterList.add(dict);
        }
        info.put("parameterList", parameterList);

        // 返回值


        Class<?> returnType = method.getReturnType();
        info.put("returnType", returnType.getSimpleName());

        boolean isSimpleType = returnType.isPrimitive() || String.class.isAssignableFrom(returnType);
        if (isSimpleType) {
            return info;
        }

        boolean isCollection = Collection.class.isAssignableFrom(returnType);
        if (isCollection) {
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();// 泛型
            Type type = parameterizedType.getActualTypeArguments()[0];


            info.put("returnList", getClassFields((Class<?>) type));

            return info;
        }


        info.put("returnList", getClassFields(returnType));

        return info;
    }


    private List<Dict> getClassFields(Class<?> cls) {
        List<Dict> list = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
            if (jsonIgnore != null) {
                continue;
            }

            Dict dict = new Dict();
            dict.put("name", field.getName());
            dict.put("type", field.getType().getSimpleName());

            OpenApiField f = field.getAnnotation(OpenApiField.class);
            if (f != null) {
                dict.put("required", f.required());
                dict.put("desc", f.desc());
                dict.put("demo", f.demo());
            } else {

                Msg msg = field.getAnnotation(Msg.class);
                if (msg != null) {
                    dict.put("desc", msg.value());
                }
            }

            list.add(dict);
        }
        return list;
    }

}
