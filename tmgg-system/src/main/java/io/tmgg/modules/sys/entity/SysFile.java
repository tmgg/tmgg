
package io.tmgg.modules.sys.entity;

import io.tmgg.lang.RequestTool;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpServletRequest;

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
     * 文件存储位置（1:阿里云，2:腾讯云，3:minio，4:本地）
     */
    private Integer fileLocation;

    /**
     * 文件仓库
     */
    private String fileBucket;

    /**
     * 文件名称（上传时候的文件名）
     */
    private String fileOriginName;



    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件大小kb
     */
    private Long fileSizeKb;
    private Integer fileSize;
    /**
     * 文件大小信息，计算后的
     */
    private String fileSizeInfo;

    /**
     * 存储到bucket的名称（文件唯一标识id）
     */
    private String fileObjectName;

    /**
     * 存储路径
     */
    private String filePath;


    @Transient
    public String getName(){
        return fileOriginName;
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
