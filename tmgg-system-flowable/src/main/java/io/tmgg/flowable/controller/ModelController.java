
package io.tmgg.flowable.controller;

import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.assignment.Identity;
import io.tmgg.flowable.entity.ConditionVariable;
import io.tmgg.flowable.entity.FlowModel;
import io.tmgg.flowable.service.MyFlowModelService;
import cn.moon.lang.web.Option;
import cn.moon.lang.web.Pageable;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.SpringTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程模型控制器
 */
@RestController
@RequestMapping("flowable/model")
public class ModelController {


    @Resource
    private MyFlowModelService service;


    @GetMapping("page")
    public Result page(String keyword, Pageable pageable) {
        return Result.ok().data(service.findAll(keyword, pageable));
    }


    @GetMapping("delete")
    public Result delete(@RequestParam String id) throws SQLException {
        service.deleteById(id);
        return Result.ok();
    }

    @PostMapping("save")
    public Result save(@RequestBody FlowModel param) throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        service.save(param);
        return Result.ok();
    }

    @PostMapping("saveContent")
    public Result saveContent(@RequestBody FlowModel param) throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        FlowModel save = service.saveContent(param);
        return Result.ok().data(save);
    }


    @PostMapping("deploy")
    public Result deploy(@RequestBody FlowModel param) throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        FlowModel flowModel = service.saveContent(param);

        service.deploy(flowModel.getCode(), flowModel.getName(), flowModel.getContent());

        return Result.ok().msg("部署成功");
    }


    @GetMapping("detail")
    public Result detail(String id) {
        FlowModel model = service.findOne(id);
        if (StringUtils.isBlank(model.getContent())) {
            String xml = service.createDefaultModel(model.getCode(), model.getName());
            model.setContent(xml);
        }


        List<ConditionVariable> conditionVariable = model.getConditionVariableList();

        Map<String, Object> data = new HashMap<>();
        data.put("model", model);
        data.put("conditionVariable", conditionVariable);


        Result result = Result.ok().data(data);


        return result;
    }


    @GetMapping("assignmentTypeList")
    public Result assignmentTypeList() {
        Map<String, AssignmentTypeProvider> beans = SpringTool.getBeansOfType(AssignmentTypeProvider.class);

        Collection<AssignmentTypeProvider> values = beans.values().stream().sorted(Comparator.comparing(AssignmentTypeProvider::getOrder)).collect(Collectors.toList());

        return Result.ok().data(values);
    }

    @GetMapping("assignmentObjectTree")
    public Result assignmentObjectTree(String code) {
        if (StringUtils.isEmpty(code) || code.equals("undefined")) {
            return Result.err().msg("请输入code");
        }
        Map<String, AssignmentTypeProvider> providerMap = SpringTool.getBeansOfType(AssignmentTypeProvider.class);

        AssignmentTypeProvider handler = null;
        for (AssignmentTypeProvider provider : providerMap.values()) {
            if (provider.getCode().equals(code)) {
                handler = provider;
                break;
            }
        }

        Assert.state(handler != null, "主数据处理器不存在");
        Collection<Identity> identityList = handler.findAll();


        return Result.ok().data(identityList);
    }

    @GetMapping("javaDelegateOptions")
    public Result javaDelegateOptions() {
        Collection<JavaDelegate> beans = SpringTool.getBeans(JavaDelegate.class);

        List<Option> options = Option.convertList(beans, b -> b.getClass().getName(), b -> b.getClass().getSimpleName() + " (" + b.getClass().getName() + ")");

        return Result.ok().data(options);
    }


}
