
package io.tmgg.modules.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.SysProp;
import io.tmgg.lang.DownloadTool;
import io.tmgg.lang.RequestTool;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.dao.SysFileDao;
import io.tmgg.modules.sys.entity.SysFile;
import io.tmgg.modules.sys.file.FileOperator;
import io.tmgg.modules.sys.file.enums.FileLocationEnum;
import io.tmgg.modules.sys.file.result.SysFileResult;
import io.tmgg.web.consts.SymbolConstant;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    @Resource
    private FileOperator fileOperator;

    @Resource
    private SysFileDao sysFileDao;

    @Resource
    private SysProp sysProp;

    public String getPreviewUrl(String fileId, HttpServletRequest request) {
        String baseUrl = sysProp.getSiteInfo().getUrl();
        if(StrUtil.isBlank(baseUrl)){
            baseUrl = RequestTool.getBaseUrl(request);
        }

        baseUrl = baseUrl.trim();

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
        String fileId = String.valueOf(IdUtil.getSnowflake().nextId());

        // 获取文件后缀
        String fileSuffix = null;

        if (ObjectUtil.isNotEmpty(originalFilename)) {
            fileSuffix = StrUtil.subAfter(originalFilename, SymbolConstant.PERIOD, true);
        }
        // 生成文件的最终名称
        String finalName = fileId + SymbolConstant.PERIOD + fileSuffix;

        // 存储文件
        fileOperator.storageFile(null, finalName, bytes);

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


    public SysFileResult getFileResult(String fileId) throws Exception {
        Assert.hasText(fileId,"文件id不存在");
        byte[] fileBytes;
        // 获取文件名
        SysFile sysFile = sysFileDao.findOne(fileId);
        Assert.notNull(sysFile, "文件不存在");
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
}