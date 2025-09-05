#FROM openjdk:17-alpine 官方已经不推荐使用了
FROM amazoncorretto:17

# 安装依赖工具（wget/unzip）
RUN yum install -y wget unzip

# 下载思源黑体（Adobe OTF 版本）
RUN wget https://github.com/adobe-fonts/source-han-sans/releases/download/2.004R/SourceHanSansSC.zip \
    && unzip SourceHanSansSC.zip -d /tmp/fonts \
    && mkdir -p /usr/share/fonts/sourcehansans \
    && cp /tmp/fonts/OTF/SimplifiedChinese/*.otf /usr/share/fonts/sourcehansans/ \
    && rm -rf /tmp/fonts SourceHanSansSC.zip

# 刷新字体缓存
RUN fc-cache -fv

# 验证字体是否安装成功（可选）
RUN fc-list | grep "Source Han Sans"
