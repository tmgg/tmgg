
package io.tmgg.modules.sys.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;

/**
 * 系统操作日志表
 *
 *
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysLog extends BaseEntity {


    @Msg("模块")
    @Column(length = 50)
    private String module;

    @NotNull
    private String name;

    @NotNull
    private String account;




    @NotNull
    private Boolean success;


    @Column(length = 10000)
    private String message;


    private String ip;

    /**
     * 地址
     */
    private String location;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 请求地址
     */
    private String url;



    /**
     * 请求参数
     */
    @Lob
    private String param;


}
