package io.tmgg.modules.api.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.Build;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.entity.*;
import io.tmgg.modules.api.service.ApiAccountService;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("apiAccount")
public class ApiAccountController extends BaseController<ApiAccount> {

    @Resource
    private ApiAccountService service;

    @Resource
    private ApiAccountResourceService accountResourceService;





    @GetMapping("docInfo")
    public AjaxResult docInfo(String id) {
        ApiAccount acc = service.findOne(id);
        List<ApiResource> list = accountResourceService.findByAccount(acc);


        for (ApiResource r : list) {
            List<ApiResourceArgument> parameterList = r.getParameterList();
            List<ApiResourceArgumentReturn> returnList = r.getReturnList();
            r.putExtData("parameterList", parameterList);
            r.putExtData("returnList", returnList);
        }


        Dict resultData = new Dict();
        resultData.put("apiList", list);
        resultData.put("frameworkVersion", Build.getFrameworkVersion());

        return AjaxResult.ok().data(resultData);
    }


}
