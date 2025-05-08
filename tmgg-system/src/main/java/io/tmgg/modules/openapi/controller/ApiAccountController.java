package io.tmgg.modules.openapi.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.Build;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.openapi.entity.*;
import io.tmgg.modules.openapi.service.ApiAccountService;
import io.tmgg.modules.openapi.service.ApiResourceService;
import io.tmgg.modules.openapi.service.OpenApiAccountResourceService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("openApiAccount")
public class ApiAccountController extends BaseController<OpenApiAccount> {

    @Resource
    private ApiAccountService service;

    @Resource
    private OpenApiAccountResourceService accountResourceService;



    private JpaQuery<OpenApiAccount> buildQuery(CommonQueryParam param) {
        JpaQuery<OpenApiAccount> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), OpenApiAccount.Fields.name,OpenApiAccount.FIELD_ID, OpenApiAccount.Fields.accessIp);
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<OpenApiAccount> q = buildQuery(param);

        Page<OpenApiAccount> page = service.findAll(q, pageable);


        return AjaxResult.ok().data(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody OpenApiAccount param) throws Exception {
        OpenApiAccount result = service.saveOrUpdate(param);
        return AjaxResult.ok().data(result.getId()).msg("保存成功");
    }





    @GetMapping("docInfo")
    public AjaxResult docInfo(String id) {
        OpenApiAccount acc = service.findOne(id);
        List<OpenApiResource> list = accountResourceService.findByAccount(acc);


        for (OpenApiResource r : list) {
            List<OpenApiResourceArgument> parameterList = r.getParameterList();
            List<OpenApiResourceArgumentReturn> returnList = r.getReturnList();
            r.putExtData("parameterList",parameterList);
            r.putExtData("returnList",returnList);
        }


        Dict resultData = new Dict();
        resultData.put("apiList", list);
        resultData.put("frameworkVersion", Build.FRAMEWORK_VERSION);

        return AjaxResult.ok().data(resultData);
    }


}
