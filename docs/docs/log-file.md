# 日志文件
## 介绍
日志文件使用的logback,主框架中内置两个配置文件
- logback-spring.xml 默认日志文件，在框架jar包中，包含了一些基础Appender
  - file 记录到文件
  - console 打印在控制台
- logback-project.xml 项目日志文件，在实际项目中，定义一些特殊的logger

## 快速自定义logger
可直接在application.yml 配置，参考如下
```
logging:
  file:
    path: /logs/app-log
  level:
    io:
      tmgg:
        framework:
          aop:
            SysLogAop: trace
```
