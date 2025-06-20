package io.tmgg.modules.job.controller;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.lang.ann.field.Field;
import io.tmgg.lang.SpringTool;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.ann.field.FieldInfo;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobParamFieldProvider;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.quartz.QuartzService;
import io.tmgg.modules.job.service.SysJobLogService;
import io.tmgg.modules.job.service.SysJobService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
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
    private QuartzService quartzService;

    @Resource
    private SysJobLogService sysJobLogService;



    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws SchedulerException {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.searchText(searchText, SysJob.Fields.name, SysJob.Fields.jobClass);
        Page<SysJob> page = service.findAll(q, pageable);

        List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
        Map<JobKey, JobExecutionContext> currentlyExecutingJobsMap = currentlyExecutingJobs.stream().collect(Collectors.toMap(ctx -> ctx.getJobDetail().getKey(), ctx -> ctx));


        for (SysJob job : page) {
            SysJobLog latest = sysJobLogService.findLatest(job);
            if (latest != null) {
                job.putExtData("beginTime", latest.getBeginTime());
                job.putExtData("endTime", latest.getEndTime());
                job.putExtData("jobRunTime", latest.getJobRunTimeLabel());
                job.putExtData("result", latest.getResult());
            }

            if (job.getEnabled()) {
                JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
                JobExecutionContext ctx = currentlyExecutingJobsMap.get(jobKey);
                if (ctx != null) {
                    job.putExtData("executing", true);
                    job.putExtData("fireTime", ctx.getFireTime());
                }
            }
        }


        return AjaxResult.ok().data(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysJob param, RequestBodyKeys updateFields) throws Exception {
        Class.forName(param.getJobClass());

        service.saveOrUpdate(param,updateFields);
        return AjaxResult.ok().msg("操作成功");
    }


    @PostMapping("delete")
    public AjaxResult delete(String id) throws SchedulerException {
        service.deleteJob(id);
        return AjaxResult.ok().msg("删除成功");
    }

    @HasPermission(label = "执行一次")
    @GetMapping("triggerJob")
    public AjaxResult triggerJob(String id) throws SchedulerException, ClassNotFoundException {
        SysJob job = service.findOne(id);
        quartzService.triggerJob(job);

        return AjaxResult.ok().msg("执行一次命令已发送");
    }


    @GetMapping("jobClassOptions")
    public AjaxResult jobClassList() {
        Collection<String> basePackages = SpringTool.getBasePackageClasses().stream().map(Class::getPackageName).toList();

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
                        option.setLabel(name + " " + jobDesc.label());
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
            option.setLabel(option.getLabel() + " " + jobDesc.label());

            FieldInfo[] params = jobDesc.params();
            for (FieldInfo param : params) {
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
