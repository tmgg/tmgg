
package io.tmgg.modules.sys.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.modules.sys.service.SysConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
public class LocalFileOperator implements FileOperator {

    public static final String DEFAULT_BUCKET = "defaultBucket";




    private String savePath;

    public LocalFileOperator(String savePath) {
        log.info("本地文件保存地址为: {}", savePath);
        this.savePath = savePath;
        FileUtil.mkdir(savePath);
    }

    public String getCurrentSavePath() {

        return savePath;
    }

    @Override
    public void storageFile(String bucketName, String key, byte[] bytes) {
        bucketName = StrUtil.emptyToDefault(bucketName, DEFAULT_BUCKET);

        // 判断bucket存在不存在
        String bucketPath = getCurrentSavePath() + File.separator + bucketName;
        if (!FileUtil.exist(bucketPath)) {
            FileUtil.mkdir(bucketPath);
        }

        // 存储文件
        String absoluteFile = getCurrentSavePath() + File.separator + bucketName + File.separator + key;
        FileUtil.writeBytes(bytes, absoluteFile);
    }

    @Override
    public void storageFile(String bucketName, String key, InputStream inputStream) {
        bucketName = StrUtil.emptyToDefault(bucketName, DEFAULT_BUCKET);
        // 判断bucket存在不存在
        String bucketPath = getCurrentSavePath() + File.separator + bucketName;
        if (!FileUtil.exist(bucketPath)) {
            FileUtil.mkdir(bucketPath);
        }

        // 存储文件
        String absoluteFile = getCurrentSavePath() + File.separator + bucketName + File.separator + key;
        FileUtil.writeFromStream(inputStream, absoluteFile);
    }

    @Override
    public byte[] getFileBytes(String bucketName, String key) throws Exception {
        bucketName = StrUtil.emptyToDefault(bucketName, DEFAULT_BUCKET);
        // 判断文件存在不存在
        String absoluteFile = getCurrentSavePath() + File.separator + bucketName + File.separator + key;
        if (!FileUtil.exist(absoluteFile)) {
            String message = StrUtil.format("文件不存在,bucket={},key={} ,path={}", bucketName, key, absoluteFile);
            throw new FileNotFoundException(message);
        } else {
            return FileUtil.readBytes(absoluteFile);
        }
    }

    @Override
    public InputStream getFileStream(String bucketName, String key) throws Exception {
        bucketName = StrUtil.emptyToDefault(bucketName, DEFAULT_BUCKET);
        // 判断文件存在不存在
        String absoluteFile = getCurrentSavePath() + File.separator + bucketName + File.separator + key;
        if (!FileUtil.exist(absoluteFile)) {
            String message = StrUtil.format("文件不存在,bucket={},key={} ,path={}", bucketName, key, absoluteFile);
            throw new FileNotFoundException(message);
        }
        return FileUtil.getInputStream(absoluteFile);
    }


    @Override
    public void deleteFile(String bucketName, String key) {
        bucketName = StrUtil.emptyToDefault(bucketName, DEFAULT_BUCKET);
        // 判断文件存在不存在
        String file = getCurrentSavePath() + File.separator + bucketName + File.separator + key;
        if (!FileUtil.exist(file)) {
            return;
        }

        // 删除文件
        FileUtil.del(file);
    }
}
