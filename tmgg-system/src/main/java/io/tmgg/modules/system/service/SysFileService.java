
package io.tmgg.modules.system.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.common.enums.MaterialType;
import io.tmgg.config.SysProp;
import io.tmgg.lang.DownloadTool;
import io.tmgg.lang.IdTool;
import io.tmgg.lang.ImgTool;
import io.tmgg.modules.system.dao.SysFileDao;
import io.tmgg.modules.system.entity.SysFile;
import io.tmgg.modules.system.file.FileOperator;
import io.tmgg.web.consts.SymbolConstant;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;

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

    public static final String[] IMAGE_SIZE_KEY = {"sm", "md", "lg"}; // 小图，中，大图
    public static final int[] IMAGE_SIZE = {400, 800, 1200}; // 小图，中，大图

    @Resource
    SysProp sysProp;


    @Resource
    private SysFileDao sysFileDao;


    @Resource
    private SysConfigService sysConfigService;


    public String getPreviewUrl(String id, HttpServletRequest request) {
        String baseUrl = sysConfigService.getOrParseBaseUrl(request);

        return baseUrl + getPreviewUrl(id);
    }

    /**
     * 获得预览相对url
     *
     * @param fileId
     * @return
     */
    public String getPreviewUrl(String fileId) {
        return PREVIEW_URL_PATTERN.replace("{id}", fileId);
    }

    public String getDownloadUrl(String fileId, HttpServletRequest request) {
        String baseUrl = sysConfigService.getOrParseBaseUrl(request);

        return baseUrl + DOWNLOAD_URL_PATTERN.replace("{id}", fileId);
    }

    public void deleteById(String id) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        sysFileDao.deleteById(id);

        // 删除具体文件
        fileOperator.delete(sysFile.getObjectName());
    }

    public SysFile uploadFile(byte[] data, String originalFilename) throws Exception {
        return this.uploadFile(new ByteArrayInputStream(data), originalFilename, data.length);
    }


    public SysFile uploadFile(MultipartFile file) throws Exception {
        InputStream is = file.getInputStream();
        String name = file.getOriginalFilename();
        return this.uploadFile(is, name, file.getSize());
    }


    public SysFile uploadFile(InputStream is, String originalFilename, long size) throws Exception {
        log.info("上传文件:{} 大小:{}", originalFilename, FileUtil.readableFileSize(size));

        // 获取文件后缀
        String suffix = null;
        if (ObjectUtil.isNotEmpty(originalFilename)) {
            suffix = StrUtil.subAfter(originalFilename, SymbolConstant.PERIOD, true);
        }

        if (StrUtil.isEmpty(suffix)) {
            Assert.state(is.markSupported(), "输入流必须支持标记");
            is.mark(64);
            suffix = FileTypeUtil.getType(is);
            is.reset();
        }

        Assert.hasText(suffix, "解析后缀失败");
        Assert.state(sysProp.getAllowUploadFiles().contains(suffix), "文件格式" + suffix + "不允许上次");

        String id = IdTool.uuidV7();

        // 生成文件的最终名称
        String objectName = buildObjectName(id, suffix, null);

        // 存储文件信息
        SysFile sysFile = new SysFile();
        sysFile.setTempId(id);
        sysFile.setOriginName(originalFilename);
        sysFile.setSuffix(suffix);
        sysFile.setSize(size);
        sysFile.setObjectName(objectName);

        MediaType mediaType = MediaTypeFactory.getMediaType("." + suffix).orElse(null);
        if (mediaType != null) {
            sysFile.setMimeType(mediaType.toString());
        }
        sysFile.setType(MaterialType.parseBySuffix(suffix));


        File tempFile = FileUtil.createTempFile("." + suffix, true);
        FileUtils.copyInputStreamToFile(is, tempFile);


        // 存储文件
        fileOperator.saveFile(objectName, tempFile);
        if (sysFile.getType() == MaterialType.IMAGE) {
            for (int i = 0; i < IMAGE_SIZE.length; i++) {
                int imageSize = IMAGE_SIZE[i];
                File tempImageFile = ImgTool.scale(tempFile, imageSize);
                if (tempImageFile != null) {
                    String imageObjectName = buildObjectName(id, suffix, imageSize);
                    fileOperator.saveFile(imageObjectName, tempImageFile);
                    FileUtil.del(tempImageFile);
                }
            }
        }
        FileUtil.del(tempFile);

        sysFile = sysFileDao.save(sysFile);


        return sysFile;
    }

    @NotNull
    private String buildObjectName(String id, String suffix, Integer size) {
        String baseName = id;
        if (size != null) {
            baseName += "_" + size;
        }
        return DateUtil.format(new Date(), "yyyyMM") + "/" + baseName + "." + suffix;
    }


    public SysFile getFileAndStream(String fileId) throws Exception {
        Assert.hasText(fileId, "文件id不能为空");
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);
        Assert.notNull(sysFile, "文件数据记录不存在");
        // 返回文件字节码
        InputStream is = fileOperator.getFileStream(sysFile.getObjectName());
        sysFile.setInputStream(is);

        return sysFile;
    }

    public InputStream getFileStream(String fileId) throws Exception {
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);

        return fileOperator.getFileStream(sysFile.getObjectName());
    }


    public void preview(String id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //根据文件id获取文件信息结果集
        SysFile sysFile = this.getFileAndStream(id);
        String fileSuffix = sysFile.getSuffix().toLowerCase();
        InputStream is = sysFile.getInputStream();
        if (StrUtil.equalsAny(fileSuffix, PREVIEW_TYPES)) {
            IOUtils.copy(is, resp.getOutputStream());
            IOUtils.closeQuietly(is, resp.getOutputStream());
        } else {
//            // 无法预览, 则下载

//            String fileName = f.getOriginName();
//            DownloadTool.download(fileName, is, f.getFileSize(), response);

            resp.setContentType("text/html;charset=utf-8");
            PrintWriter writer = resp.getWriter();

            String downloadUrl = this.getDownloadUrl(id, req);
            writer.write("文件无法预览！ <a href='%s' >点击下载</a>".formatted(downloadUrl));
            writer.flush();
            writer.close();
        }

    }


    public void download(String id, HttpServletResponse response) throws Exception {
        // 获取文件信息结果集
        SysFile f = this.getFileAndStream(id);
        String fileName = f.getOriginName();
        DownloadTool.download(fileName, f.getInputStream(), f.getSize(), response);
    }

    /**
     * 下载到所属服务器
     *
     * @param id
     * @param localFile
     * @return
     * @throws Exception
     */
    public File downloadToLocal(String id, File localFile) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        fileOperator.downloadFile(sysFile.getObjectName(), localFile);
        return localFile;
    }

    public File downloadToLocalTemp(String id) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        File tempFile = FileUtil.createTempFile("." + sysFile.getSuffix(), true);
        fileOperator.downloadFile(sysFile.getObjectName(), tempFile);

        return tempFile;
    }

    public SysFile findOne(String id) {
        return sysFileDao.findOne(id);
    }


    @Resource
    FileOperator fileOperator;


    public Page<SysFile> findAll(JpaQuery<SysFile> q, Pageable pageable) {
        return sysFileDao.findAll(q, pageable);
    }

    public boolean isFileExist(String id) {
        if (StrUtil.isEmpty(id)) {
            return false;
        }
        SysFile file = sysFileDao.findOne(id);
        if (file == null) {
            return false;
        }

        return fileOperator.exist(file.getObjectName());
    }
}
