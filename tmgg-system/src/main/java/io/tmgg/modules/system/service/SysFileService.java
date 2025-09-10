
package io.tmgg.modules.system.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.config.SysProp;
import io.tmgg.lang.DownloadTool;
import io.tmgg.modules.system.dao.SysFileDao;
import io.tmgg.modules.system.entity.SysFile;
import io.tmgg.modules.system.file.FileOperator;
import io.tmgg.web.consts.SymbolConstant;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.Optional;

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
        fileOperator.delete(sysFile.getFileObjectName());
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
            Assert.hasText(suffix,"解析后缀失败");
            Assert.state(sysProp.getAllowUploadFiles().contains(suffix), "文件格式" + suffix + "不允许上次");
        }

        if(StrUtil.isEmpty(suffix)){
            suffix = FileTypeUtil.getType(is);
            is.reset();
        }

        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType("."+suffix);




        String id = IdUtil.getSnowflakeNextIdStr();

        // 生成文件的最终名称
        String objectName = DateUtil.format(new Date(),"yyyyMM") + "/" + id + "." + suffix;


        // 存储文件
        fileOperator.save(objectName, is);


        // 存储文件信息
        SysFile sysFile = new SysFile();
        sysFile.setTempId(id);
        sysFile.setFileOriginName(originalFilename);
        sysFile.setFileSuffix(suffix);
        sysFile.setFileSize(size);
        sysFile.setFileObjectName(objectName);

        if(mediaType.isPresent()){
            sysFile.setMimeType(mediaType.get().toString());
        }

        sysFile = sysFileDao.save(sysFile);


        return sysFile;
    }


    public SysFile getFileAndStream(String fileId) throws Exception {
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


    public void preview(String id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //根据文件id获取文件信息结果集
        SysFile sysFile = this.getFileAndStream(id);
        String fileSuffix = sysFile.getFileSuffix().toLowerCase();
        InputStream is = sysFile.getInputStream();
        if (StrUtil.equalsAny(fileSuffix, PREVIEW_TYPES)) {
            IOUtils.copy(is, resp.getOutputStream());
            IOUtils.closeQuietly(is, resp.getOutputStream());
        } else {
//            // 无法预览, 则下载

//            String fileName = f.getFileOriginName();
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
        String fileName = f.getFileOriginName();
        DownloadTool.download(fileName, f.getInputStream(), f.getFileSize(), response);
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
        fileOperator.downloadFile(sysFile.getFileObjectName(), localFile);
        return localFile;
    }

    public File downloadToLocalTemp(String id) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        File tempFile = FileUtil.createTempFile("." + sysFile.getFileSuffix(), true);
        fileOperator.downloadFile(sysFile.getFileObjectName(), tempFile);

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

        return fileOperator.exist(file.getFileObjectName());
    }
}
