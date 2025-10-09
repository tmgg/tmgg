package io.tmgg.config;

import io.tmgg.modules.system.file.FileOperator;
import io.tmgg.modules.system.file.LocalFileOperator;
import io.tmgg.modules.system.file.MinioFileOperator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class FileConfig {

    @Resource
    MinioProp minioProp;


    @Resource
    SysProp sysProp;

    @Bean
    public FileOperator fileOperator() {
        if (minioProp.getEnable()) {
            log.info("配置文件服务为minio模式");
            return new MinioFileOperator(minioProp.getUrl(), minioProp.getAccessKey(), minioProp.getSecretKey(), minioProp.getBucketName());
        }
        log.info("本地文件模式");
        return new LocalFileOperator(sysProp.getFileUploadPath());
    }

}
