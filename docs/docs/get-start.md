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
参考模板中的Car.java, 使用@Msg注解描述实体及字段

### 生成代码
启动前后端，默认密码会打印到控制台

网页登录后台，使用代码生成功能



