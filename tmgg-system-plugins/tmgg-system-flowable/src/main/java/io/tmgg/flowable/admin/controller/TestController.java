package io.tmgg.flowable.admin.controller;

import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.admin.entity.SysFlowableModel;
import io.tmgg.flowable.admin.service.SysFlowableModelService;
import io.tmgg.dto.AjaxResult;
import jakarta.annotation.Resource;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("flowable/test")
public class TestController {

    @Resource
    private SysFlowableModelService myFlowModelService;

    @Resource
    private FlowableManager fm;


    @GetMapping("get")
    public AjaxResult get(String id) {
        Assert.hasText(id, "id不能为空");
        SysFlowableModel model = myFlowModelService.findOne(id);
        return AjaxResult.ok().data(model);
    }

    @PostMapping("submit")
    public AjaxResult submit(@RequestBody Map<String,Object> params) {
        String bizKey = params.get("id").toString();
        String modelCode = (String) params.get("modelCode");

        fm.start(modelCode,bizKey, params);

        return AjaxResult.ok().msg("提交测试流程成功");
    }

}
