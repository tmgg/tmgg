package io.tmgg.job.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.BasePackage;
import io.tmgg.SysProp;
import io.tmgg.data.Field;
import io.tmgg.data.FieldAnn;
import io.tmgg.job.JobDesc;
import io.tmgg.job.JobParamFieldProvider;
import io.tmgg.job.entity.SysJob;
import io.tmgg.job.quartz.QuartzManager;
import io.tmgg.job.service.SysJobService;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("job")
public class SysJobController {

    @Resource
    private SysJobService service;

    @Resource
    private Scheduler scheduler;

    @Resource
    private QuartzManager quartzManager;

    @Resource
    private SysProp sysProp;


    @HasPermission
    @GetMapping("page")
    public AjaxResult page(SysJob param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        Page<SysJob> page = service.findByExampleLike(param, pageable);


        List<Map<String, Object>> list = page.stream().map(job -> {
            Map<String, Object> map = new HashMap<>();
            BeanUtil.copyProperties(job, map);

            if (job.getEnabled()) {
                try {
                    TriggerKey triggerKey = TriggerKey.triggerKey(job.getTriggerKey());


                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    if (trigger != null) {
                        map.put("previousFireTime", trigger.getPreviousFireTime());
                        map.put("nextFireTime", trigger.getNextFireTime());
                    }


                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }


            return map;
        }).collect(Collectors.toList());


        return AjaxResult.ok().data(new PageImpl<>(list, pageable, page.getTotalElements()));
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysJob param) throws Exception {
        Class.forName(param.getJobClass());

        SysJob result = service.saveOrUpdate(param);
        return AjaxResult.ok().msg("操作成功").data(result.getId());
    }


    @PostMapping("delete")
    public AjaxResult delete(String id) throws SchedulerException {
        service.deleteJob(id);
        return AjaxResult.ok().msg("删除成功");
    }

    @Remark("执行一次")
    @HasPermission
    @GetMapping("triggerJob")
    public AjaxResult triggerJob(String id) throws SchedulerException, ClassNotFoundException {
        SysJob job = service.findOne(id);
        quartzManager.triggerJob(job);

        return AjaxResult.ok().msg("执行一次命令已发送");
    }


    @GetMapping("jobClassOptions")
    public AjaxResult jobClassList() {
        Set<Class<?>> list = ClassUtil.scanPackageBySuper(BasePackage.BASE_PACKAGE, Job.class);

        Set<Class<?>> list2 = ClassUtil.scanPackageBySuper(sysProp.getBasePackageClass().getPackageName(), Job.class);
        list.addAll(list2);

        List<Option> options = list.stream()
                .filter(cls -> {
                    int mod = cls.getModifiers();
                    return !Modifier.isAbstract(mod) && !Modifier.isInterface(mod);
                })
                .map(cls -> {
                    String name = cls.getName();

                    Option option = new Option();
                    option.setValue(name);
                    option.setLabel(name);

                    JobDesc jobDesc = cls.getAnnotation(JobDesc.class);
                    if(jobDesc !=null){
                        option.setLabel(name + " " + jobDesc.name());
                    }

                    return option;
                }).sorted(Comparator.comparing(Option::getLabel)).collect(Collectors.toList());

        return AjaxResult.ok().data(options);
    }

    @PostMapping("getJobParamFields")
    public AjaxResult getJobParamFields(String className, @RequestBody Map<String, Object> jobData) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, JsonProcessingException {
        Class<?> jobCls = Class.forName(className);
        String name = jobCls.getName();

        Option option = new Option();
        option.setValue(name);

        option.setLabel(name);


        List<Field> result = new ArrayList<>();
        JobDesc jobDesc = jobCls.getAnnotation(JobDesc.class);
        if (jobDesc != null) {
            option.setLabel(option.getLabel() + " " + jobDesc.name());

            FieldAnn[] params = jobDesc.params();
            for (FieldAnn param : params) {
                Field d = new Field();
                d.setName(param.name());
                d.setLabel(param.label());
                d.setRequired(param.required());
                result.add(d);
            }
        }
        Class<? extends JobParamFieldProvider> provider = jobDesc.paramsProvider();
        if (provider != null) {
            int mod = provider.getModifiers();
            boolean isInterface = Modifier.isInterface(mod);
            if (!isInterface) {
                JobParamFieldProvider bean = SpringTool.getBean(provider);
                List<Field> fields = bean.getFields(jobDesc, jobData);
                result.addAll(fields);
            }
        }

        return AjaxResult.ok().data(result);
    }


    @ExceptionHandler(JobPersistenceException.class)
    public AjaxResult ex(JobPersistenceException e) {
        return AjaxResult.err().msg(e.getMessage());

    }

}
