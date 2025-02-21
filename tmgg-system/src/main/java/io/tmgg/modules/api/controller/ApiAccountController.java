package io.tmgg.modules.api.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import io.tmgg.lang.DownloadTool;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.api.Api;
import io.tmgg.modules.api.ApiResource;
import io.tmgg.modules.api.entity.ApiAccount;
import io.tmgg.modules.api.service.ApiAccountService;
import io.tmgg.modules.api.service.ApiResourceService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("apiAccount")
public class ApiAccountController extends BaseController<ApiAccount> {

    @Resource
    ApiAccountService service;

    @Resource
    ApiResourceService apiResourceService;



    private JpaQuery<ApiAccount> buildQuery(CommonQueryParam param) {
        JpaQuery<ApiAccount> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), "name", ApiAccount.Fields.accessIp, ApiAccount.Fields.perms);
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<ApiAccount> q = buildQuery(param);

        Page<ApiAccount> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody ApiAccount param) throws Exception {
        if (param.isNew()) {
            param.setAppSecret(RandomUtil.randomString(32));
        }

        ApiAccount result = service.saveOrUpdate(param);
        return AjaxResult.ok().data(result.getId()).msg("保存成功");
    }



    @GetMapping("resourceOptions")
    public AjaxResult resourceOptions() {
        Collection<ApiResource> all = apiResourceService.findAll();

        List<Option> options = all.stream().map(r -> {
            Api api = r.getApi();
            return Option.builder().label(api.name()).value(api.url()).build();
        }).collect(Collectors.toList());

        return AjaxResult.ok().data(options);
    }




}
