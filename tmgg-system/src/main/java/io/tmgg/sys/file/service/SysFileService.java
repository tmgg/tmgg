
package io.tmgg.sys.file.service;

import io.tmgg.lang.CodeException;
import io.tmgg.lang.DownloadTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.file.FileOperator;
import io.tmgg.sys.file.dao.SysFileDao;
import io.tmgg.sys.file.entity.SysFile;
import io.tmgg.sys.file.enums.FileLocationEnum;
import io.tmgg.sys.file.enums.SysFileExceptionEnum;
import io.tmgg.sys.file.result.SysFileResult;
import io.tmgg.web.consts.SymbolConstant;
import io.tmgg.web.context.requestno.RequestNoContext;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 文件信息表 服务实现类
 */
@Service
@Slf4j
public class SysFileService {

    public static final String[] PREVIEW_TYPES = new String[]{
            "jpg", "jpeg", "png", "gif", "pdf",
    };

    @Resource
    private FileOperator fileOperator;

    // 是否添加用户水印
    @Value("${addUserMask.enable:false}")
    private boolean addUserMask;

    @Value("${addUserMask.fileBucket:}")
    private String addUserMaskFileBucket;

    @Resource
    SysFileDao sysFileDao;


    public void deleteById(String id) throws Exception {
        SysFile sysFile = sysFileDao.findOne(id);
        sysFileDao.deleteById(id);

        // 删除具体文件
        this.fileOperator.deleteFile(sysFile.getFileBucket(), sysFile.getFileObjectName());
    }


    /***
     *
     * @param file
     *
     * @throws IOException
     */
    public SysFile uploadFile(MultipartFile file) throws IOException {
        return this.uploadFile(file.getBytes(), file.getOriginalFilename());
    }

    public SysFile uploadFile(byte[] bytes, String originalFilename) {
        String fileId = String.valueOf(IdUtil.getSnowflake().nextId());

        // 获取文件后缀
        String fileSuffix = null;

        if (ObjectUtil.isNotEmpty(originalFilename)) {
            fileSuffix = StrUtil.subAfter(originalFilename, SymbolConstant.PERIOD, true);
        }
        // 生成文件的最终名称
        String finalName = fileId + SymbolConstant.PERIOD + fileSuffix;

        // 存储文件
        try {
            fileOperator.storageFile(null, finalName, bytes);
        } catch (Exception e) {
            throw new CodeException(SysFileExceptionEnum.ERROR_FILE);
        }

        // 计算文件大小kb
        long fileSizeKb = Convert.toLong(NumberUtil.div(new BigDecimal(bytes.length), BigDecimal.valueOf(1024))
                .setScale(0, RoundingMode.HALF_UP));

        //计算文件大小信息
        String fileSizeInfo = FileUtil.readableFileSize(bytes.length);

        // 存储文件信息
        SysFile sysFile = new SysFile();
        sysFile.setId(fileId);
        sysFile.setFileLocation(FileLocationEnum.LOCAL.getCode());
        sysFile.setFileBucket(null);
        sysFile.setFileObjectName(finalName);
        sysFile.setFileOriginName(originalFilename);
        sysFile.setFileSuffix(fileSuffix);
        sysFile.setFileSizeKb(fileSizeKb);
        sysFile.setFileSize(bytes.length);
        sysFile.setFileSizeInfo(fileSizeInfo);
        return sysFileDao.save(sysFile);
    }


    public SysFileResult getFileInfoResult(String fileId) {
        byte[] fileBytes;
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);
        if (sysFile == null) {
            throw new CodeException(SysFileExceptionEnum.NOT_EXISTED_FILE);
        }
        try {
            // 返回文件字节码
            fileBytes = fileOperator.getFileBytes(sysFile.getFileBucket(), sysFile.getFileObjectName());

        } catch (Exception e) {
            log.error(">>>{} 获取文件流异常，请求号为：{}，具体信息为：{}", fileOperator.getClass().getName(), RequestNoContext.get(), e.getMessage());
            throw new CodeException(SysFileExceptionEnum.FILE_STREAM_ERROR);
        }

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


    public void assertFile(String id) {
        SysFile sysFile = sysFileDao.findOne(id);
        if (ObjectUtil.isEmpty(sysFile)) {
            throw new CodeException(SysFileExceptionEnum.NOT_EXISTED);
        }
    }


    public void preview(String id, HttpServletResponse response) throws IOException {
        byte[] fileBytes;
        //根据文件id获取文件信息结果集
        SysFileResult fileResult = this.getFileInfoResult(id);
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


    public void download(String id, HttpServletResponse response) {
        // 获取文件信息结果集
        SysFileResult sysFileResult = this.getFileInfoResult(id);
        String fileName = sysFileResult.getFileOriginName();
        byte[] fileBytes = sysFileResult.getFileBytes();
        DownloadTool.download(fileName, fileBytes, response);
    }

    // 添加水印
    private byte[] pressText(HttpServletResponse response, boolean isPublic, byte[] fileBytes, String fileBucket, String fileSuffix) {
        boolean isImage = false;
        //设置contentType
        if (StrUtil.endWithIgnoreCase(fileSuffix, "jpg")) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            isImage = true;
        } else if (StrUtil.endWithIgnoreCase(fileSuffix, "png")) {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            isImage = true;
        }

        if (isImage && addUserMask && (StrUtil.isEmpty(addUserMaskFileBucket) || StrUtil.split(addUserMaskFileBucket, ",").contains(fileBucket))) {
            ByteArrayInputStream is = new ByteArrayInputStream(fileBytes);
            Image image = ImgUtil.read(is);

            int fontSize = 50;
            Font font = new Font("黑体", Font.PLAIN, fontSize);
            if (!isPublic) {
                Subject subject = SecurityUtils.getSubject();


                int y = 0;
                image = ImgUtil.pressText(image, "保密文件，请勿外传", Color.WHITE, font, 0, y, 0.5f);
                y += fontSize;

                if (!subject.isAuthenticated()) {
                    image = ImgUtil.pressText(image, "未登录，缺少token", Color.WHITE, font, 0, y, 0.5f);
                } else {

                    String unitName = subject.getUnitName();
                    if (unitName != null) {
                        image = ImgUtil.pressText(image, unitName, Color.WHITE, font, 0, y, 0.5f);
                        y += fontSize;
                    }

                    image = ImgUtil.pressText(image, subject.getName(), Color.WHITE, font, 0, y, 0.5f);
                }
            }

            fileBytes = ImgUtil.toBytes(image, fileSuffix);
        }
        return fileBytes;
    }


    public Object findByExampleLike(SysFile param, Pageable pageable) {
        JpaQuery<SysFile> query = new JpaQuery<>();
        query.likeExample(param);

        return this.sysFileDao.findAll(query, pageable);
    }

    public Object findOne(String id) {
        return sysFileDao.findOne(id);
    }
}
