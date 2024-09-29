
package io.tmgg.config;

import io.tmgg.sys.file.FileOperator;
import io.tmgg.sys.file.LocalFileOperator;
import io.tmgg.sys.file.LocalFileProperties;
import io.tmgg.sys.file.MinioFileOperator;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.sys.service.SysConfigService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 文件存储的配置
 * <p>
 * 默认激活本地文件存储
 */
@Configuration
public class FileConfig {


    /**
     * 默认文件存储的位置
     */


    @Resource
    SysConfigService sysConfigService;

    /**
     * 本地文件操作客户端
     */
    @Bean
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


        // 默认使用本地文件
        LocalFileProperties localFileProperties = new LocalFileProperties();
        String fileUploadPathForWindows = sysConfigService.getFileUploadPathForWindows();
        if (ObjectUtil.isNotEmpty(fileUploadPathForWindows)) {
            localFileProperties.setLocalFileSavePathWin(fileUploadPathForWindows);
        }

        String fileUploadPathForLinux = sysConfigService.getFileUploadPathForLinux();
        if (ObjectUtil.isNotEmpty(fileUploadPathForLinux)) {
            localFileProperties.setLocalFileSavePathLinux(fileUploadPathForLinux);
        }
        return new LocalFileOperator(localFileProperties);
    }

}
