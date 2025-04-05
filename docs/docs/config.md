---
title: 配置说明
---


## 基础配置

### id生产策略
默认的id生成策略是uuid， 可通过实体类型上增加注解@CustomId改变

支持增加前缀，prefix，
可这只长度

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
    },
    {
      "application": "system",
      "id": "sysUser",
      "name": "用户管理",
      "code": "sysUser",
      "router": "/system/student",
      "type": "MENU",
      "status": "ENABLE",
      "visible": "Y",
      "icon": "UserOutlined",
      "seq": "2"
    }
  ]
}

```

如果希望控制数据是否更新，可添加 $update字段，值为true或false

默认是通过id来判断是否存在，以便判断是新增还是更新操作，如果想通过其他字段判断唯一性，可通过$pk字段，如 $pk:"code"

