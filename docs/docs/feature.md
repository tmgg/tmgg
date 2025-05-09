# 特殊功能

除了传统的后台管理系统，为了方便开发，增加了一些特殊功能

## 时间范围

前端可使用组件 FieldDateRange, 参考ISO 8601 时间间隔格式

后端构造查询条件时，可使用

```java
        JpaQuery<SysLog> q=new JpaQuery<>();
        q.betweenIsoDateRange("createTime",dateRange);

```

## 冗余字段

页面显示通常会包含更多的字段，例如一个实体存了orgId，但希望显示orgLabel

通常会增加一个字段 orgLabel, 并使用@Transient注解标注，然后手动设置。

【自动设置】，可以在orgId字段上使用自动注解 @AutoAppend

```
   @AutoAppendField( AutoAppendOrgLabelStrategy.class)
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

