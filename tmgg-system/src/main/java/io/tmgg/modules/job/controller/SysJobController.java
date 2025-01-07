package io.tmgg.modules.job.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.BasePackage;
import io.tmgg.data.Field;
import io.tmgg.data.FieldDesc;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobParamFieldProvider;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.quartz.QuartzManager;
import io.tmgg.modules.job.service.SysJobService;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import lombok.Data;
import org.quartz.*;
import org.springframework.data.domain.Page;
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

    @Data
    public static class QueryParam{
        String keyword;
    }
    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(@RequestBody QueryParam param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws SchedulerException {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.searchText(param.getKeyword(), SysJob.Fields.name, SysJob.Fields.jobClass);
        Page<SysJob> page = service.findAll(q, pageable);

        List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
        Map<JobKey, JobExecutionContext> currentlyExecutingJobsMap = currentlyExecutingJobs.stream().collect(Collectors.toMap(ctx -> ctx.getJobDetail().getKey(), ctx -> ctx));


        for (SysJob job : page) {
            if (job.getEnabled()) {
                JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
                JobExecutionContext ctx = currentlyExecutingJobsMap.get(jobKey);
                if (ctx != null) {
                    job.putExtField("executing", true);
                    job.putExtField("fireTime", ctx.getFireTime());
                }

                List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);

                if (CollUtil.isNotEmpty(triggersOfJob)) {
                    Trigger trigger = triggersOfJob.get(0);
                    job.putExtField("previousFireTime", trigger.getPreviousFireTime());
                    job.putExtField("nextFireTime", trigger.getNextFireTime());
                }

            }
        }


        return AjaxResult.ok().data(page);
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
        Collection<String> basePackages = BasePackage.getBasePackages();

        Set<Class<?>> list = new HashSet<>();
        for (String basePackage : basePackages) {
            Set<Class<?>> list1 = ClassUtil.scanPackageBySuper(basePackage, Job.class);
            list.addAll(list1);
        }

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
                    if (jobDesc != null) {
                        option.setLabel(name + " " + jobDesc.name());
                    }

                    return option;
                }).collect(Collectors.toList());

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

            FieldDesc[] params = jobDesc.params();
            for (FieldDesc param : params) {
                Field d = new Field();
                d.setName(param.name());
                d.setLabel(param.label());
                d.setRequired(param.required());
                result.add(d);
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
        }


        return AjaxResult.ok().data(result);
    }


    @ExceptionHandler(JobPersistenceException.class)
    public AjaxResult ex(JobPersistenceException e) {
        return AjaxResult.err().msg(e.getMessage());

    }

}
