#FROM openjdk:17-alpine
FROM openjdk:17.0.2-oracle
# 黑体， 宋体等字体文件
ADD assets/fonts/ /usr/share/fonts/chinese/
ADD assets/fonts/ /$JAVA_HOME/jre/lib/fonts/



# 阶段3 运行环境, 减少镜像大小
FROM openjdk:17.0.2-oracle

# 黑体
ADD fonts/simhei.ttf /usr/share/fonts/chinese/
ADD fonts/simhei.ttf /$JAVA_HOME/jre/lib/fonts/

# 宋体
ADD fonts/simsun.ttc /usr/share/fonts/chinese/
ADD fonts/simsun.ttc /$JAVA_HOME/jre/lib/fonts/
