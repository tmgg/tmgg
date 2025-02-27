package io.tmgg.modules.openapi.controller;

import cn.hutool.core.util.RandomUtil;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.openapi.ApiResource;
import io.tmgg.modules.openapi.OpenApi;
import io.tmgg.modules.openapi.entity.OpenApiAccount;
import io.tmgg.modules.openapi.service.ApiAccountService;
import io.tmgg.modules.openapi.service.ApiResourceService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Map<String, String> permMap = apiResourceService.findAll().stream().collect(Collectors.toMap(t->t.getOpenApi().action(), t->t.getOpenApi().name()));
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
        List<OpenApi> apis = list.stream().map(ApiResource::getOpenApi).filter(openApi -> acc.getPerms().contains(openApi.action())).toList();

        List<Map<String, Object>> mapList = apis.stream().map(api -> {
            Map<String, Object> map = new HashMap<>();
            map.put("action", api.action());
            map.put("name", api.name());
            map.put("desc", api.desc());
            return map;
        }).toList();

        return AjaxResult.ok().data(mapList);
    }


}
