
package io.tmgg.modules.system.entity;

import cn.hutool.core.io.FileUtil;
import io.tmgg.lang.RequestTool;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

/**
 * 文件信息
 *
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysFile extends BaseEntity {


    /**
     * 文件名称（上传时候的文件名）
     */
    private String fileOriginName;



    /**
     * 文件后缀
     */
    private String fileSuffix;

    private Long fileSize;


    /**
     * 存储到bucket的名称（文件唯一标识id）
     */
    @NotNull
    private String fileObjectName;

    @Column(length = 20)
    private String mimeType;


    @Transient
    private InputStream inputStream;

    @Transient
    public String getName(){
        return fileOriginName;
    }

    @Transient
    public String getFileSizeInfo() {
        if(fileSize != null){
            return FileUtil.readableFileSize(fileSize);
        }
        return null;
    }

    @Transient
    public String getUrl(){
        HttpServletRequest request = RequestTool.currentRequest();
        if(request != null){
            String baseUrl = RequestTool.getBaseUrl(request);
            return baseUrl + "/sysFile/preview/" + getId();
        }

        return null;
    }



}
