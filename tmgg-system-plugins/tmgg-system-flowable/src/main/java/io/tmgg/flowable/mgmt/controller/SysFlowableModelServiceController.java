
package io.tmgg.flowable.mgmt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.assignment.Identity;
import io.tmgg.flowable.mgmt.entity.ConditionVariable;
import io.tmgg.flowable.mgmt.entity.SysFlowableModel;
import io.tmgg.flowable.mgmt.service.SysFlowableModelService;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程模型控制器
 */
@RestController
@RequestMapping("flowable/model")
public class SysFlowableModelServiceController {


    @Resource
    private SysFlowableModelService service;




    @HasPermission("flowableModel:page")
    @RequestMapping("page")
    public AjaxResult page(String searchText, Pageable pageable) throws Exception {
        Page<SysFlowableModel> page = service.findAll(searchText, pageable);
        return service.autoRender(page);
    }


    @HasPermission("flowableModel:delete")
    @GetMapping("delete")
    public AjaxResult delete(@RequestParam String id) {
        service.deleteById(id);
        return AjaxResult.ok();
    }

    @HasPermission("flowableModel:save")
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysFlowableModel param, RequestBodyKeys keys) throws Exception {
        service.saveOrUpdate(param,keys);
        return AjaxResult.ok();
    }
    @HasPermission("flowableModel:design")
    @PostMapping("saveContent")
    public AjaxResult saveContent(@RequestBody SysFlowableModel param) {
        SysFlowableModel save = service.saveContent(param);
        return AjaxResult.ok().data(save);
    }

    @HasPermission("flowableModel:deploy")
    @PostMapping("deploy")
    public AjaxResult deploy(@RequestBody SysFlowableModel param) throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        SysFlowableModel sysFlowableModel = service.saveContent(param);

        service.deploy(sysFlowableModel.getCode(), sysFlowableModel.getName(), sysFlowableModel.getContent());

        return AjaxResult.ok().msg("部署成功");
    }


    @GetMapping("detail")
    public AjaxResult detail(String id) {
        SysFlowableModel model = service.findOne(id);
        if (StringUtils.isBlank(model.getContent())) {
            String xml = service.createDefaultModel(model.getCode(), model.getName());
            model.setContent(xml);
        }


        List<ConditionVariable> conditionVariable = model.getConditionVariableList();

        Map<String, Object> data = new HashMap<>();
        data.put("model", model);
        data.put("conditionVariable", conditionVariable);


        AjaxResult result = AjaxResult.ok().data(data);


        return result;
    }


    @GetMapping("assignmentTypeList")
    public AjaxResult assignmentTypeList() {
        Map<String, AssignmentTypeProvider> beans = SpringTool.getBeansOfType(AssignmentTypeProvider.class);

        Collection<AssignmentTypeProvider> values = beans.values().stream().sorted(Comparator.comparing(AssignmentTypeProvider::getOrder)).collect(Collectors.toList());

        return AjaxResult.ok().data(values);
    }

    @GetMapping("assignmentObjectTree")
    public AjaxResult assignmentObjectTree(String code) {
        if (StringUtils.isEmpty(code) || code.equals("undefined")) {
            return AjaxResult.err().msg("请输入code");
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


        return AjaxResult.ok().data(identityList);
    }

    @GetMapping("javaDelegateOptions")
    public AjaxResult javaDelegateOptions() {
        Collection<JavaDelegate> beans = SpringTool.getBeans(JavaDelegate.class);

        List<Option> options = Option.convertList(beans, b -> b.getClass().getName(), b -> b.getClass().getSimpleName() + " (" + b.getClass().getName() + ")");

        return AjaxResult.ok().data(options);
    }


}
