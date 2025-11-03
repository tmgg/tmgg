package io.tmgg.modules.api.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.Build;
import io.tmgg.modules.api.ApiErrorCode;
import io.tmgg.modules.api.service.ApiResourceService;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.entity.*;
import io.tmgg.modules.api.service.ApiAccountService;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("apiAccount")
public class ApiAccountController extends BaseController<ApiAccount> {

    @Resource
    private ApiAccountService service;

    @Resource
    private ApiAccountResourceService accountResourceService;


    @Resource
    private ApiResourceService apiResourceService;



    @GetMapping("docInfo")
    public AjaxResult docInfo(String id) {
        ApiAccount acc = service.findOne(id);
        List<ApiResource> list = accountResourceService.findByAccount(acc);
        list = apiResourceService.removeNotExist(list);

        for (ApiResource r : list) {
            List<ApiResourceArgument> parameterList = r.getParameterList();
            List<ApiResourceArgumentReturn> returnList = r.getReturnList();
            r.putExtData("parameterList", parameterList);
            r.putExtData("returnList", returnList);
        }


        Dict resultData = new Dict();
        resultData.put("apiList", list);
        resultData.put("frameworkVersion", Build.getFrameworkVersion());
        resultData.put("appId", acc.getAppId());

        List<Dict> errorList = new ArrayList<>();
        for (ApiErrorCode value : ApiErrorCode.values()) {
            errorList.add(Dict.of("code",value.getCode(),"message", value.getMessage()));
        }

        resultData.put("errorList", errorList);



        return AjaxResult.ok().data(resultData);
    }


}
