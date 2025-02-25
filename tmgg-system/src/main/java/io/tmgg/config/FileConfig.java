
package io.tmgg.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.modules.sys.file.FileOperator;
import io.tmgg.modules.sys.file.LocalFileOperator;
import io.tmgg.modules.sys.file.MinioFileOperator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


/**
 * 文件存储的配置
 * <p>
 * 默认激活本地文件存储
 */
@Configuration
public class FileConfig {




    /**
     * 本地文件操作客户端
     */
    @Bean
    @Lazy
    public FileOperator fileOperator() {
        //判断minio
        String minioUrl = SpringUtil.getProperty("file.minio.url");
        if (StrUtil.isNotBlank(minioUrl)) {
            String accessKey = SpringUtil.getProperty("file.minio.accessKey");
            String secretKey = SpringUtil.getProperty("file.minio.secretKey");
            String defaultBucketName = SpringUtil.getProperty("file.minio.defaultBucketName");
            Assert.notEmpty(defaultBucketName, "请配置：file.minio.defaultBucketName");
            return new MinioFileOperator(minioUrl, accessKey, secretKey, defaultBucketName);
        }

        return new LocalFileOperator();
    }

}
