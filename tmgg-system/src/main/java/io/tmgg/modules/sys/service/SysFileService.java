
package io.tmgg.modules.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.event.SysConfigChangeEvent;
import io.tmgg.event.SystemDataInitFinishEvent;
import io.tmgg.lang.DownloadTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.dao.SysFileDao;
import io.tmgg.modules.sys.entity.SysFile;
import io.tmgg.modules.sys.file.FileOperator;
import io.tmgg.modules.sys.file.LocalFileOperator;
import io.tmgg.modules.sys.file.MinioFileOperator;
import io.tmgg.modules.sys.file.enums.FileLocationEnum;
import io.tmgg.modules.sys.file.result.SysFileResult;
import io.tmgg.web.consts.SymbolConstant;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件信息表 服务实现类
 */
@Service
@Slf4j
public class SysFileService {

    public static final String previewUrl = "/sysFile/preview/{id}";

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

        String url = baseUrl + previewUrl.replace("{id}", fileId);
        return url;
    }


    public void deleteById(String id) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        sysFileDao.deleteById(id);

        // 删除具体文件
        this.fileOperator.deleteFile(sysFile.getFileBucket(), sysFile.getFileObjectName());
    }


    public SysFile uploadFile(MultipartFile file) throws Exception {
        return this.uploadFile(file.getBytes(), file.getOriginalFilename());
    }

    public SysFile uploadFile(byte[] bytes, String originalFilename) throws Exception {

        // 获取文件后缀
        String fileSuffix = null;

        if (ObjectUtil.isNotEmpty(originalFilename)) {
            fileSuffix = StrUtil.subAfter(originalFilename, SymbolConstant.PERIOD, true);
        }


        String fileId = IdUtil.getSnowflakeNextIdStr();

        // 生成文件的最终名称
        String finalName = fileId + SymbolConstant.PERIOD + fileSuffix;


        //计算文件大小信息
        String fileSizeInfo = FileUtil.readableFileSize(bytes.length);

        // 存储文件信息
        SysFile sysFile = new SysFile();
        sysFile.setCustomId(fileId);
        sysFile.setFileLocation(FileLocationEnum.LOCAL.getCode());
        sysFile.setFileBucket(null);
        sysFile.setFileOriginName(originalFilename);
        sysFile.setFileSuffix(fileSuffix);
        sysFile.setFileSize(bytes.length);
        sysFile.setFileSizeInfo(fileSizeInfo);
        sysFile.setFileObjectName(finalName);
        sysFile = sysFileDao.save(sysFile);

        // 存储文件
        fileOperator.storageFile(null, finalName, bytes);

        return sysFile;
    }


    public SysFileResult getFileResult(String fileId) throws Exception {
        Assert.hasText(fileId, "文件id不能为空");
        byte[] fileBytes;
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);
        Assert.notNull(sysFile, "文件数据记录不存在");
        // 返回文件字节码
        fileBytes = fileOperator.getFileBytes(sysFile.getFileBucket(), sysFile.getFileObjectName());


        SysFileResult sysFileResult = new SysFileResult();
        BeanUtil.copyProperties(sysFile, sysFileResult);
        sysFileResult.setFileBytes(fileBytes);

        return sysFileResult;
    }

    public InputStream getFileStream(String fileId) throws Exception {
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);

        return fileOperator.getFileStream(sysFile.getFileBucket(), sysFile.getFileObjectName());
    }


    public void preview(String id, HttpServletResponse response) throws Exception {
        byte[] fileBytes;
        //根据文件id获取文件信息结果集
        SysFileResult fileResult = this.getFileResult(id);
        //获取文件后缀
        String fileSuffix = fileResult.getFileSuffix().toLowerCase();

        if (StrUtil.equalsAny(fileSuffix, PREVIEW_TYPES)) {
            //获取文件字节码
            fileBytes = fileResult.getFileBytes();
            response.getOutputStream().write(fileBytes);
            return;
        }


        // 无法预览, 则下载
        this.download(id, response);
    }


    public void download(String id, HttpServletResponse response) throws Exception {
        // 获取文件信息结果集
        SysFileResult sysFileResult = this.getFileResult(id);
        String fileName = sysFileResult.getFileOriginName();
        byte[] fileBytes = sysFileResult.getFileBytes();
        DownloadTool.download(fileName, fileBytes, response);
    }


    public Object findByExampleLike(SysFile param, Pageable pageable) {
        JpaQuery<SysFile> query = new JpaQuery<>();
        query.likeExample(param);

        return this.sysFileDao.findAll(query, pageable);
    }

    public Object findOne(String id) {
        return sysFileDao.findOne(id);
    }


    private void init() {
        boolean minioSet = sysConfigService.isValueSet("file.minio.enable", "file.minio.url", "file.minio.accessKey", "file.minio.secretKey", "file.minio.bucketName");

        if (minioSet) {
            boolean enable = sysConfigService.getBoolean("file.minio.enable");
            log.info("mino服务是否开启:{}", enable);
            if (enable) {
                String minioUrl = sysConfigService.getStr("file.minio.url");
                String accessKey = sysConfigService.getStr("file.minio.accessKey");
                String secretKey = sysConfigService.getStr("file.minio.secretKey");
                String bucketName = sysConfigService.getStr("file.minio.bucketName");
                log.info("配置文件服务为minio模式");
                this.fileOperator = new MinioFileOperator(minioUrl, accessKey, secretKey, bucketName);
                return;
            }
        }

        log.info("配置文件服务为本地文件模式");
        this.fileOperator = new LocalFileOperator(sysConfigService.getFileUploadPath());
    }
}
