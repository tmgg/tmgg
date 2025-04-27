
package io.tmgg.modules.sys.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.event.SysConfigChangeEvent;
import io.tmgg.event.SystemDataInitFinishEvent;
import io.tmgg.lang.DownloadTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.controller.SysFileController;
import io.tmgg.modules.sys.dao.SysFileDao;
import io.tmgg.modules.sys.entity.SysFile;
import io.tmgg.modules.sys.file.FileOperator;
import io.tmgg.modules.sys.file.LocalFileOperator;
import io.tmgg.modules.sys.file.MinioFileOperator;
import io.tmgg.web.consts.SymbolConstant;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件服务类
 *
 * 由于会被其他模块使用，不继承BaseService,减少干扰
 */
@Service
@Slf4j
public class SysFileService {

    public static final String PREVIEW_URL_PATTERN = "/sysFile/preview/{id}";

    public static final String[] PREVIEW_TYPES = new String[]{
            "jpg", "jpeg", "png", "gif", "pdf",
    };

    private FileOperator fileOperator;

    @Resource
    private SysFileDao sysFileDao;


    @Resource
    private SysConfigService sysConfigService;


    @EventListener
    public void onSystemDataInitFinish(SystemDataInitFinishEvent e) {
        log.info("系统初始化完成事件触发，接着初始化文件服务");
        init();
    }


    @EventListener
    public void onSysConfigChange(SysConfigChangeEvent e) {
        log.info("系统配置事件触发，判断是否重新初始化文件服务");
        init();
    }

    public String getPreviewUrl(String fileId, HttpServletRequest request) {
        String baseUrl = sysConfigService.getOrParseBaseUrl(request);

        return baseUrl + PREVIEW_URL_PATTERN.replace("{id}", fileId);
    }


    public void deleteById(String id) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        sysFileDao.deleteById(id);

        // 删除具体文件
        this.fileOperator.delete(sysFile.getFileObjectName());
    }


    public SysFile uploadFile(MultipartFile file) throws Exception {
        InputStream is = file.getInputStream();
        String name = file.getOriginalFilename();
        return this.uploadFile(is, name, file.getSize());
    }

    public SysFile uploadFile(InputStream is, String originalFilename, long size) throws Exception {
        Assert.notNull(fileOperator, "文件存储模式未初始化");

        log.info("上传文件:{} 大小:{}", originalFilename, FileUtil.readableFileSize(size));


        // 获取文件后缀
        String fileSuffix = null;

        if (ObjectUtil.isNotEmpty(originalFilename)) {
            fileSuffix = StrUtil.subAfter(originalFilename, SymbolConstant.PERIOD, true);
        }


        String fileId = IdUtil.getSnowflakeNextIdStr();

        // 生成文件的最终名称
        String finalName = fileId + SymbolConstant.PERIOD + fileSuffix;


        // 存储文件
        fileOperator.save(finalName, is);


        // 存储文件信息
        SysFile sysFile = new SysFile();
        sysFile.setCustomId(fileId);
        sysFile.setStorageType(parseStorageType());
        sysFile.setFileOriginName(originalFilename);
        sysFile.setFileSuffix(fileSuffix);
        sysFile.setFileSize(size);
        sysFile.setFileObjectName(finalName);
        sysFile = sysFileDao.save(sysFile);


        return sysFile;
    }


    public SysFile getFileResult(String fileId) throws Exception {
        Assert.hasText(fileId, "文件id不能为空");
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);
        Assert.notNull(sysFile, "文件数据记录不存在");
        // 返回文件字节码
        InputStream is = fileOperator.getFileStream(sysFile.getFileObjectName());
        sysFile.setInputStream(is);

        return sysFile;
    }

    public InputStream getFileStream(String fileId) throws Exception {
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);

        return fileOperator.getFileStream(sysFile.getFileObjectName());
    }


    public void preview(String id, HttpServletResponse response) throws Exception {
        //根据文件id获取文件信息结果集
        SysFile f = this.getFileResult(id);
        //获取文件后缀
        String fileSuffix = f.getFileSuffix().toLowerCase();
        InputStream is = f.getInputStream();
        if (StrUtil.equalsAny(fileSuffix, PREVIEW_TYPES)) {
            IOUtils.copy(is, response.getOutputStream());
            IOUtils.closeQuietly(is, response.getOutputStream());
        }else {
            // 无法预览, 则下载
            String fileName = f.getFileOriginName();
            DownloadTool.download(fileName, is, f.getFileSize(), response);
        }

    }


    public void download(String id, HttpServletResponse response) throws Exception {
        // 获取文件信息结果集
        SysFile f = this.getFileResult(id);
        String fileName = f.getFileOriginName();
        DownloadTool.download(fileName, f.getInputStream(), f.getFileSize(), response);
    }


    public Page<SysFile> findByExampleLike(SysFile param, Pageable pageable) {
        JpaQuery<SysFile> query = new JpaQuery<>();
        query.likeExample(param);

        Page<SysFile> all = this.sysFileDao.findAll(query, pageable);
        return all;
    }

    public Object findOne(String id) {
        return sysFileDao.findOne(id);
    }


    private void init() {

        boolean enable = sysConfigService.getBoolean("file.minio.enable");
        log.info("获取系统参数：mino服务是否开启:{}", enable);
        if (enable) {
            String minioUrl = sysConfigService.getStr("file.minio.url");
            String accessKey = sysConfigService.getStr("file.minio.accessKey");
            String secretKey = sysConfigService.getStr("file.minio.secretKey");
            String bucketName = sysConfigService.getStr("file.minio.bucketName");

            if (StrUtil.isAllNotEmpty(minioUrl, accessKey, secretKey, bucketName)) {
                log.info("配置文件服务为minio模式");
                this.fileOperator = new MinioFileOperator(minioUrl, accessKey, secretKey, bucketName);
            } else {
                log.warn("【警告】虽然设置了minio启用，但还有参数未完成，文件服务暂不可用");
                this.fileOperator = null;
            }

            return;
        }

        log.info("配置文件服务为本地文件模式");
        this.fileOperator = new LocalFileOperator(sysConfigService.getFileUploadPath());
    }

    private Integer parseStorageType() {
        if (fileOperator != null) {
            if (fileOperator instanceof LocalFileOperator) {
                return 1;
            }
            if (fileOperator instanceof MinioFileOperator) {
                return 2;
            }
        }
        return null;
    }

    public Page<SysFile> findAll(JpaQuery<SysFile> q, Pageable pageable) {
        return sysFileDao.findAll(q,pageable);
    }
}
