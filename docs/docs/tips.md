---
title: 注意事项
layout: doc
---
## Lob
 由于升级到了springboot3， 原来的实体注解@Lob 生成的数据库字段类型为 tinytext,比较小，需要手动设置。

 如果长度不超过65535， 可直接用 @Column(length = 1234)
 否则可用 `@Column(columnDefinition = "longtext")`等

