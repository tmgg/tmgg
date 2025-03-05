# 介绍
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
## 配置pom.xml
```xml
 <dependency>
    <groupId>io.github.tmgg</groupId>
    <artifactId>tmgg-system-kettle</artifactId>
 </dependency>
```
## 配置环境变量
```yaml
tmgg:
  kettle:
    rep: "xxx"
    url: "http://127.0.0.1:8080"
    username: "cluster"
    password: "cluster"
```

# 界面操作
## 启动项目后，会在左侧菜单看到kettle菜单


