package io.tmgg.flowable.controller;

import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.entity.FlowModel;
import io.tmgg.flowable.service.MyFlowModelService;
import cn.moon.lang.web.Result;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("flowable/test")
public class TestController {

    @Resource
    private MyFlowModelService myFlowModelService;

    @Resource
    private FlowableManager fm;


    @GetMapping("get")
    public Result get(String id) {
        Assert.hasText(id, "id不能为空");
        FlowModel model = myFlowModelService.findOne(id);
        return Result.ok().data(model);
    }

    @PostMapping("submit")
    public Result submit(@RequestBody Map<String,Object> params) {
        String bizKey = (String) params.get("id");
        String modelCode = (String) params.get("modelCode");

        fm.start(modelCode,bizKey, params);

        return Result.ok();
    }

}
