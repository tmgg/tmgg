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



##  自动填充字段


页面显示通常会包含更多的字段，例如一个实体存了orgId，但希望显示orgLabel
通常会增加一个字段 orgLabel, 并使用@Transient注解标注，然后手动设置。
可以在orgId字段上使用自动注解 @AutoAppend
```java
    @AutoAppendField(AutoAppendOrgLabelStrategy.class)
    private String orgId;

    @Transient
    private String orgLabel;
```
开发者可扩展，秩序实现AutoAppendStrategy接口，
目前内置的有
- AutoAppendDictLabelStrategy 字典名称
- AutoAppendEnumLabelStrategy 枚举注释
- AutoAppendFileViewUrlStrategy 文件预览地址
- AutoAppendOrgLabelStrategy 机构名称
- AutoAppendUserLabelStrategy 用户名称



##  更新指定字段

对比save方法更新的时所有字段，改方法只更新指定字段



##  数据库自动生成备注

根据注解 @Msg



##  id生成策略
默认的id生成策略是uuid， 可通过实体类型上增加注解@CustomId改变

支持自定义前缀，长度，类型等

支持样式如下，具体可参考IdStyle枚举
- UUID
- DATETIME_UUID
- DATETIME_SEQ
- DAILY_SEQ ：每日id从1重新计数。例子： 用户表，prefix="USR_", idStyle=DAILY_SEQ, length=16的情况 :USR_202504060001,USR_202504060002



##  时间范围

前端可使用组件 FieldDateRange, 参考ISO 8601 时间间隔格式
存储格式：开始时间/结束时间 如：2023-01-01/2023-01-01
后端构造查询条件时，可使用
```java
JpaQuery<SysLog> q=new JpaQuery<>();
q.betweenIsoDateRange("createTime",dateRange);
```



##  系统参数注解 @DbValue

数据库有sys_config表，可使用注解 @DbValue，使用方法类似Spring的@Value注解。

如果在系统中修改了参数，也会实时重新修改字段的值

```
@DbValue("sys.sessionIdleTime")
private int timeToIdleExpiration;
```



##  会话

框架使用session， 并缓存在内存或硬盘

代码中可直接使用HttpSession存储一些登录用户的数据

为什么使用session认证
为了集成一些第三方功能页面，如 ureport。 使用session后，不用再考虑集成认证。



##  开放接口


示例代码
```java
package io.tmgg.modules.api.defaults;

import cn.hutool.core.date.DateUtil;
import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.api.Api;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class PingApi {

	@Api(name = "测试连通性", uri = "ping", desc = "示例接口，为了测试，会返回pong")
	public String ping(@FieldInfo(label = "信息", len = 50) String msg) {
		return "pong:" + msg;
	}

	@Api(name = "获得服务器时间", uri = "time")
	public TimeInfo time() {
		return new TimeInfo(DateUtil.now(), System.currentTimeMillis());
	}

	@Data
	@AllArgsConstructor
	public static class TimeInfo {
		@FieldInfo(label = "格式化时间")
		String time;

		@FieldInfo(label = "时间戳")
		long timestamp;
	}
}

```

##  定时作业

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
@JobDesc(label = "示例作业-发送系统状态", params = {@FieldInfo(name = "msg", label = "打印信息")})
public class DemoJob implements Job {

    private static final Logger log = JobTool.getLogger();


    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        log.info("开始执行任务-邮件发送系统状态");

        // 获取参数
        JobDataMap data = JobTool.getData(e);
        String msg = data.getString("msg");

        System.out.println(msg);

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



