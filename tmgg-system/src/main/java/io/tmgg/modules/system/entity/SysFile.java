
package io.tmgg.modules.system.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.framework.data.fill.FillDictText;
import io.tmgg.lang.RequestTool;
import io.tmgg.common.enums.MaterialType;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.util.Date;

/**
 * 文件信息
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysFile extends BaseEntity {

    /**
     * 文件名称（上传时候的文件名）
     */
    @Column(name = "file_origin_name", length = 100)
    private String originName;

    /**
     * 存储到bucket的名称, 支持目录， 如 2024/abc.jpg
     */
    @NotNull
    @Column(name = "file_object_name")
    private String objectName;

    /**
     * 文件后缀
     */
    @Column(name = "file_suffix", length = 10)
    private String suffix;

    @Column(name = "file_size")
    private Long size;


    @Column(length = 20)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    private MaterialType type;

    @org.springframework.data.annotation.Transient
    @FillDictText(typeCode = "materialType")
    private String typeLabel;

    private String title;
    private String description;
    private String hash;

    /**
     * 原始路径，针对那种互联网地址上传的
     */
    private String origUrl;


    @Transient
    private InputStream inputStream;

    @Transient
    public String getName() {
        return originName;
    }

    @Transient
    public String getSizeInfo() {
        if (size != null) {
            return FileUtil.readableFileSize(size);
        }
        return null;
    }

    @Transient
    public String getUrl() {
        HttpServletRequest request = RequestTool.currentRequest();
        if (request != null) {
            String baseUrl = RequestTool.getBaseUrl(request);
            return baseUrl + "/sysFile/preview/" + getId();
        }

        return null;
    }


}
