
package io.tmgg.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.modules.sys.entity.SysConfig;
import io.tmgg.modules.sys.file.FileOperator;
import io.tmgg.modules.sys.file.LocalFileOperator;
import io.tmgg.modules.sys.file.MinioFileOperator;
import io.tmgg.modules.sys.service.SysConfigService;
import jakarta.annotation.Resource;
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





}
