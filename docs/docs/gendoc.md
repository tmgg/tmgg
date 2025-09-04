##  注入json请求的keys

 如果需要获取前端提交数据的keys

 通常用于更新指定字段

 可通过在controller的方法参数中增加 `RequestBodyKeys  updateFields`

 示例
 ```
    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody T input, RequestBodyKeys updateFields) throws Exception {
          service.saveOrUpdate(input,updateFields);
        return AjaxResult.ok().msg("保存成功");
    }
 ```



##  前后端交互式，分页从1开始



##  动态显示字段


app端请求时隐藏字段， 如createUser，updateTime等字段

使用示例:
```java
@JsonIgnoreForApp
private String updateUser;
```



##  更新指定字段
<p>
对比save方法更新的时所有字段，只更新指定字段



##  直接更新指定字段
不会先find，再更新
对比save方法更新的时所有字段，改方法只更新指定字段
<p>
注意：主要用于更新单个实体的字段， 不能更新多对多等关联关系



##  数据库自动生成备注

根据注解 @Remark



##  id生成策略
默认的id生成策略是uuid， 可通过实体类型上增加注解@CustomId改变
<p>
支持自定义前缀，长度，类型等
<p>
支持样式如下，具体可参考IdStyle枚举
- UUID
- DATETIME_UUID
- DATETIME_SEQ
- DAILY_SEQ ：每日id从1重新计数。例子： 用户表，prefix="USR_", idStyle=DAILY_SEQ, length=16的情况 :USR_202504060001,USR_202504060002



##  时间范围
<p>
前端可使用组件 FieldDateRange, 参考ISO 8601 时间间隔格式
存储格式：开始时间/结束时间 如：2023-01-01/2023-01-01
后端构造查询条件时，可使用
```java
JpaQuery<SysLog> q=new JpaQuery<>();
q.betweenIsoDateRange("createTime",dateRange);
```
</p>



##  定义字段为数据字典

在字段上增加 @DictField 注解



##  会话

框架使用session， 并缓存在内存或硬盘

代码中可直接使用HttpSession存储一些登录用户的数据

为什么使用session认证
为了集成一些第三方功能页面，如 ureport。 使用session后，不用再考虑集成认证。



##  开放接口


@see DefaultApi

##  作业调度

示例代码
```java
package io.tmgg.modules.job.builtin;

import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobTool;
import org.quartz.*;
import org.slf4j.Logger;

/**
 * 示例作业
 */
@DisallowConcurrentExecution // 不允许并发则加这个注解
@JobDesc(label = "示例作业", params = {@FieldInfo(name = "msg", label = "打印信息")})
public class DemoJob implements Job {

    private static final Logger log = JobTool.getLogger();


    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        log.info("开始执行任务");

        // 获取参数
        JobDataMap data = JobTool.getData(e);
        String msg = data.getString("msg");


        System.out.println("控制台打印：" +msg);
        log.info("日志打印信息：{}", msg);

        e.setResult("结果：成功");
    }
}

```

##  初始化数据

初始化的数据可以放到resources/database目录,系统启动时回自动解析保存入库

数据格式为json, key为实体名称，value为数据数组。每个字段都对应实体，如果字段是枚举，填写枚举值即可
例如
```json

{
  "SysMenu": [
    {
      "application": "system",
      "id": "sysOrg",
      "name": "机构管理",
      "code": "sysOrg",
      "router": "/system/org",
      "type": "MENU",
      "status": "ENABLE",
      "visible": "Y",
      "icon": "ApartmentOutlined",
      "seq": "1"
    }
  ]
}

```
特殊字段
- $update true|false   控制数据是否更新
- $pk     String       默认是通过id来判断是否存在，以便判断是新增还是更新操作，如果想通过其他字段判断唯一性，如 $pk:"code"



