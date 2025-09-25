# 后台管理开发框架

基于 antd5,spring3的后台管理框架

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tmgg/tmgg-system-parent)


使用示例模板快速开始： https://github.com/tmgg/tmgg-demo.git

## 功能介绍

- 基于角色的权限控制
- 基于实体的代码生成，支持多种模板
- 开放接口，支持生成接口文档，方便第三方调用
- session持久话，使用web容器的session，并可持久话到硬盘
- 作业调度，支持参数
- 流程引擎，模型设计
- ureport报表集成，方便快速出报表
- kettle集成，方便数据抽取任务调度
- 大量前端组件，方便表单，表格显示
- 大量前后端交互组件
-

## 常用的库推荐
- pdf： itextpdf
- excel: poi

# 快速上手

## 1 开发环境要求
- JDK17 以上

[下载1](https://adoptium.net/temurin/releases?version=17)
[下载2](https://www.azul.com/downloads/?version=java-17-lts#zulu)
[下载3](http://jdk.java.net/java-se-ri/17)

- JPA支持的数据库，如MySql 8.x 或 5.7.11+ 以上版本 或 Oracle、PostgreSQL、Sql Server

- NODE 20或以上版本

## 2 项目模板
### 下载模板
源码中包含项目模板，template-backend， 前端模板位于 web-monorepo/template下

需改下版本号

### 修改数据库配置
修改 application-dev.yml ，application-prod.yml 中相关数据库配置

### 创建实体
创建实体，并使用@Remark 备注

### 生成代码
启动前后端，默认密码会打印到控制台

网页登录后台，使用代码生成功能

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
对比save方法更新的时所有字段，只更新指定字段



##  直接更新指定字段
不会先find，再更新
对比save方法更新的时所有字段，改方法只更新指定字段
注意：主要用于更新单个实体的字段， 不能更新多对多等关联关系



##  数据库自动生成备注

根据注解 @Remark



##  id生成策略
默认的id生成策略是uuid， 可通过实体类型上增加注解@CustomId改变
支持自定义前缀，长度，类型等
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
JpaQuery q=new JpaQuery();
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








# 流程引擎
## 默认表单地址
 
默认页面为：/flowable/{key}?id=xxx


# 扩展模块 - 支付

app端需没有实例化控制器，需继承 PaymentController, 并设置RequestMapping


示例
```java
package cn.crec.venue.venue.app.controler;

import io.tmgg.payment.PaymentController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("rest/payment")
public class PaymentAppController extends PaymentController {


}


```

前端支付页面，uniapp示例
```
<template>
  <view style="padding:32rpx">
    <u-toast ref="uToast"></u-toast>

    <view style="display: flex;align-items: center;flex-direction: column">
      <view>
        <u--text :text="previewOrder.description"></u--text>
      </view>
      <u-gap></u-gap>
      <view>
        <u--text size="32" mode="price" :text="previewOrder.amount / 100"></u--text>
      </view>
    </view>
    <u-gap></u-gap>   
    <u-radio-group
        v-model="selectedPaymentChannelId"
        placement="column"
        @change="onPaymentChannelChange"
    >
      <u-radio
          :customStyle="{marginBottom: '8px'}"
          v-for="(item, index) in paymentChannelList"
          :key="index"
          :label="item.name"
          :name="item.id"
      >
      </u-radio>
    </u-radio-group>

    <u-gap></u-gap>
    <u-gap></u-gap>
    <u-button @click="callFrontPay" text="确认支付" type="primary"></u-button>

  </view>
</template>

<script>
import http from '@/common/vmeitime-http/interface';

export default {
  data() {
    return {
      orderId: null, // 业务订单id

      // 业务标识， 如订单业务，充值业务等
      bizCode: null,

      // 选择的支付渠道
      selectedPaymentChannelId: null,

	  // 支付成功后返回页面
	  returnPage: '/',

      paymentChannelList: [],

      previewOrder: {
        amount: 0
      },

    }
  },

  onLoad: function (options) {
    this.orderId = options.orderId
    this.bizCode = options.bizCode
	this.returnPage = decodeURIComponent( options.returnPage)
    this.getPreviewOrder()
    this.getPaymentChannelList()
  },
  methods: {
    getPreviewOrder() {
      http.get('/rest/payment/previewOrder', {orderId: this.orderId, bizCode: this.bizCode}).then(response => {
        this.previewOrder = response.data.data
      })
    },

    getPaymentChannelList() {
      http.get('/rest/payment/channelList', {orderId: this.orderId, bizCode: this.bizCode}).then(response => {
        this.paymentChannelList = response.data.data
		if(this.paymentChannelList.length > 0){
			this.selectedPaymentChannelId = this.paymentChannelList[0].id
		}
      })
    },
    onPaymentChannelChange(v) {
      this.selectedPaymentChannelId = v;
    },


    callFrontPay() {
      let params = {orderId: this.orderId, bizCode: this.bizCode, channelId: this.selectedPaymentChannelId};
      http.get('/rest/payment/createOrder', params).then(response => {
        let payInfo = response.data.data
        uni.requestPayment({
          ...payInfo,
          package: payInfo.packageValue,
          success: this.paySuccess,
          fail: this.payFail
        })
      })
    },

    paySuccess(e) {
	  const url = this.returnPage
      this.$refs.uToast.show({
        message: '支付成功',
		complete(){
			uni.navigateTo({
				url:url
			})
		}
      });
    },
    payFail(e) {
      this.$refs.uToast.show({
        message: '支付失败',
      });
    }

  }
}
</script>


```



# 扩展插件 - Kettle
模块功能
- 对kettle存储库的增删查操作
- 对作业状态、日志的查看
- 定时调度

# Kettle 安装
## 安装插件
需要给kettle安装插件，插件地址 https://github.com/tmgg/kettle-carte-plugin

## 启动 Carte
windows 上启动 kettle 目录下的 Carte.bat。
为了避免乱码，建议在该文件前增加 set OPT= -Dfile.encoding=utf-8

# 项目中使用
## 配置pom.xml文件
```xml
 <dependency>
    <groupId>io.github.tmgg</groupId>
    <artifactId>tmgg-system-kettle</artifactId>
 </dependency>
```


## 配置yaml文件
```yaml
tmgg:
  kettle:
    rep: "xxx"
    url: "http://127.0.0.1:8080"
    username: "cluster"
    password: "cluster"
```

## 界面操作
启动项目后，会在左侧菜单看到kettle菜单




# 常见问题

## 如何设置某个请求不需要登录？

方法1、在方法上增加@Public注解

方法2、在配置文件中增加exclude配置（ide会自动提示）

## 如何覆盖框架页面-？

在项目中创建同路径的页面即可

## 如何查看管理员密码，密码丢失后怎么办？

首次运行程序时候，会自动创建管理员，并将密码打印在控制台。
如果后期网络密码，需在数据库中将用户表（sys_user)的管理员（superAdmin)记录的密码设置为空。然后启动后台，新密码将自动生成并打印在控制台

## 为什么没用redis
系统主要是管理系统，尽量少依赖中间件，方便开发和部署。

## 推荐部署工具

推荐使用docker-admin，支持源码构建、部署


# MD5
根据当前的代码分析工具提示，以及Google的guava框架提示，应该使用更好的摘要算法， 如SHA256


哈希算法 | 摘要大小 | 设计 | 碰撞抵抗能力 | 安全性
-- | -- | -- | -- | --
SHA-1 | 160位 | 迭代式；80轮 | 易发生碰撞 | 不安全
MD5 | 128位 | 迭代式；64轮 | 容易发生碰撞 | 不安全
SHA-256 | 256位 | 迭代式；64轮 | 高度抵抗碰撞 | 最安全
