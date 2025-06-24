# 日志文件
## 介绍
日志文件使用springboot自带的logback ，包含了一些基础Appender
- file 记录到文件
- console 打印在控制台

## 快速自定义logger
可直接在application.yml 配置，参考如下
```
logging:
  file:
    path: /data/logs/app-log
  level:
    io:
      tmgg:
        framework:
          aop:
            SysLogAop: trace
```
