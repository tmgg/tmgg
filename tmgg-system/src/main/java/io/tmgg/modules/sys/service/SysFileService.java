
package io.tmgg.modules.sys.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.framework.dbconfig.DbValue;
import io.tmgg.lang.DownloadTool;
import io.tmgg.modules.sys.dao.SysFileDao;
import io.tmgg.modules.sys.entity.SysFile;
import io.tmgg.modules.sys.file.FileOperator;
import io.tmgg.modules.sys.file.LocalFileOperator;
import io.tmgg.modules.sys.file.MinioFileOperator;
import io.tmgg.web.consts.SymbolConstant;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 * 文件服务类
 * <p>
 * 由于会被其他模块使用，不继承BaseService,减少干扰
 */
@Service
@Slf4j
public class SysFileService {

    public static final String PREVIEW_URL_PATTERN = "/sysFile/preview/{id}";
    public static final String DOWNLOAD_URL_PATTERN = "/sysFile/download/{id}";
    public static final String[] PREVIEW_TYPES = new String[]{
            "jpg", "jpeg", "png", "gif", "pdf",
    };


    @Resource
    private SysFileDao sysFileDao;


    @Resource
    private SysConfigService sysConfigService;


    public String getPreviewUrl(String fileId, HttpServletRequest request) {
        String baseUrl = sysConfigService.getOrParseBaseUrl(request);

        return baseUrl + PREVIEW_URL_PATTERN.replace("{id}", fileId);
    }
    public String getDownloadUrl(String fileId, HttpServletRequest request) {
        String baseUrl = sysConfigService.getOrParseBaseUrl(request);

        return baseUrl + DOWNLOAD_URL_PATTERN.replace("{id}", fileId);
    }

    public void deleteById(String id) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        sysFileDao.deleteById(id);

        // 删除具体文件
        getFileOperator().delete(sysFile.getFileObjectName());
    }


    public SysFile uploadFile(MultipartFile file) throws Exception {
        InputStream is = file.getInputStream();
        String name = file.getOriginalFilename();
        return this.uploadFile(is, name, file.getSize());
    }

    public SysFile uploadFile(InputStream is, String originalFilename, long size) throws Exception {

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
        getFileOperator().save(finalName, is);


        // 存储文件信息
        SysFile sysFile = new SysFile();
        sysFile.setCustomGenerateId(fileId);
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
        InputStream is = getFileOperator().getFileStream(sysFile.getFileObjectName());
        sysFile.setInputStream(is);

        return sysFile;
    }

    public InputStream getFileStream(String fileId) throws Exception {
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);

        return getFileOperator().getFileStream(sysFile.getFileObjectName());
    }


    public void preview(String id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //根据文件id获取文件信息结果集
        SysFile f = this.getFileResult(id);
        //获取文件后缀
        String fileSuffix = f.getFileSuffix().toLowerCase();
        InputStream is = f.getInputStream();
        if (StrUtil.equalsAny(fileSuffix, PREVIEW_TYPES)) {
            IOUtils.copy(is, resp.getOutputStream());
            IOUtils.closeQuietly(is, resp.getOutputStream());
        } else {
//            // 无法预览, 则下载

//            String fileName = f.getFileOriginName();
//            DownloadTool.download(fileName, is, f.getFileSize(), response);

            resp.setContentType("text/html;charset=utf-8");
            PrintWriter writer = resp.getWriter();

            String downloadUrl =  this.getDownloadUrl(id,req);
            writer.write("文件无法预览！ <a href='%s' >点击下载</a>".formatted(downloadUrl));
            writer.flush();
            writer.close();
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

    public SysFile findOne(String id) {
        return sysFileDao.findOne(id);
    }


    @DbValue("file.minio.enable")
    boolean minioEnable;


    @DbValue("file.minio.url")
    String minioUrl;

    @DbValue("file.minio.accessKey")
    String minioAccessKey;

    @DbValue("file.minio.secretKey")
    String minioSecretKey;

    @DbValue("file.minio.bucketName")
    String minioBucketName;

    public FileOperator getFileOperator() {
        if (minioEnable) {
            log.info("配置文件服务为minio模式");
            Assert.state(StrUtil.isAllNotEmpty(minioUrl, minioAccessKey, minioSecretKey, minioBucketName), "minio配置不全");

            return new MinioFileOperator(minioUrl, minioAccessKey, minioSecretKey, minioBucketName);
        }
        log.info("本地文件模式");
        return new LocalFileOperator(sysConfigService.getFileUploadPath());
    }


    private Integer parseStorageType() {
        FileOperator fileOperator = getFileOperator();
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
        return sysFileDao.findAll(q, pageable);
    }
}
