package io.tmgg.modules.api.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.Build;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.entity.*;
import io.tmgg.modules.api.service.ApiAccountService;
import io.tmgg.modules.api.service.ApiAccountResourceService;
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
public class ApiAccountController extends BaseController<ApiAccount> {

    @Resource
    private ApiAccountService service;

    @Resource
    private ApiAccountResourceService accountResourceService;



    private JpaQuery<ApiAccount> buildQuery(CommonQueryParam param) {
        JpaQuery<ApiAccount> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), ApiAccount.Fields.name, ApiAccount.FIELD_ID, ApiAccount.Fields.accessIp);
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
        ApiAccount result = service.saveOrUpdate(param);
        return AjaxResult.ok().data(result.getId()).msg("保存成功");
    }





    @GetMapping("docInfo")
    public AjaxResult docInfo(String id) {
        ApiAccount acc = service.findOne(id);
        List<ApiResource> list = accountResourceService.findByAccount(acc);


        for (ApiResource r : list) {
            List<ApiResourceArgument> parameterList = r.getParameterList();
            List<ApiResourceArgumentReturn> returnList = r.getReturnList();
            r.putExtData("parameterList",parameterList);
            r.putExtData("returnList",returnList);
        }


        Dict resultData = new Dict();
        resultData.put("apiList", list);
        resultData.put("frameworkVersion", Build.FRAMEWORK_VERSION);

        return AjaxResult.ok().data(resultData);
    }


}
