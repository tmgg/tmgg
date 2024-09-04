package io.tmgg.job.controller;

import io.tmgg.BasePackage;
import io.tmgg.job.JobRemark;
import io.tmgg.job.Param;
import io.tmgg.job.entity.SysJob;
import io.tmgg.job.quartz.QuartzManager;
import io.tmgg.job.service.SysJobService;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ClassUtil;
import org.quartz.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
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


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody SysJob param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
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


        return AjaxResult.ok().data( new PageImpl<>(list, pageable, page.getTotalElements()));
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysJob param) throws Exception {
        Class.forName(param.getJobClass());

        SysJob result = service.saveOrUpdate(param);
        return AjaxResult.ok().msg("操作成功").data( result.getId());
    }


    @GetMapping("delete")
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

        List<Option> options = list.stream()
                .filter(cls -> {
                    int mod = cls.getModifiers();
                    return !Modifier.isAbstract(mod) && !Modifier.isInterface(mod);
                })
                .map(cls -> {
                    String name = cls.getName();

                    Option option = new Option();
                    option.setValue(name);
                    String remark = RemarkTool.getRemark(cls);
                    option.setLabel(name);
                    if (remark != null) {
                        option.setLabel(name +" "+ remark);
                    }

                    List<Dict> fields = new ArrayList<>();
                    JobRemark jobRemark = cls.getAnnotation(JobRemark.class);
                    if (jobRemark != null) {
                        Param[] params = jobRemark.params();
                        for (Param param : params) {
                            Dict d = new Dict();
                            d.put("key", param.key());
                            d.put("label", param.label());
                            d.put("required", param.required());
                            fields.add(d);
                        }
                    }

                    option.setData(fields);


                    return option;
                }).sorted(Comparator.comparing(Option::getLabel)).collect(Collectors.toList());

        return AjaxResult.ok().data(options);
    }


    @ExceptionHandler(JobPersistenceException.class)
    public AjaxResult ex(JobPersistenceException e) {
        return AjaxResult.err().msg(e.getMessage());

    }

}
