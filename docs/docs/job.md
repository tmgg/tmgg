---
title: 定时作业
---
# 支持参数设置
```java


@JobDesc(name = "示例作业-发送系统状态", params = {@FieldDesc(name = "email", label = "接收邮箱", required = true)})
public class DemoJob implements Job {

    private static final Logger log = JobTool.getLogger();


    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        log.info("开始执行任务-邮件发送系统状态");

        // 获取参数
        JobDataMap data = JobTool.getData(e);
        String email = data.getString("email");
        Assert.hasText(email, "请填写邮箱"); // 抛出异常

        log.info("接收邮箱为：{}", email);

        log.info("空闲内存: {}", FileUtil.readableFileSize(SystemUtil.getFreeMemory()));
        log.info("总内存：{}", FileUtil.readableFileSize(SystemUtil.getTotalMemory()));

        log.info("模拟发送邮件中给 {} ,暂停3秒", email);
        ThreadUtil.sleep(3 * 1000);
        log.info("任务结束");
    }
}

```
