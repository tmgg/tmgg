---
title: 配置说明
---


## 基础配置

### id生产策略
默认的id生成策略是uuid， 可通过实体类型上增加注解@CustomId改变

支持自定义前缀，长度，类型等

支持样式如下，具体可参考IdStyle枚举
- UUID
- DATETIME_UUID
- DATETIME_SEQ
- DAILY_SEQ ：每日id从1重新计数。例子： 用户表，prefix="USR_", idStyle=DAILY_SEQ, length=16的情况 :USR_202504060001,USR_202504060002


## 初始化数据

初始化的数据可以放到resources/database目录
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


## 系统参数
数据库有sys_config表，可使用注解 @DbValue，使用方法类似Spring的@Value注解。

如果在系统中修改了参数，也会实时重新修改字段的值

```
@DbValue("sys.sessionIdleTime")
private int timeToIdleExpiration;
```

