
package io.tmgg.sys.log.entity;

import io.tmgg.lang.dao.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.time.LocalDateTime;

/**
 * 系统操作日志表
 *

 *
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysOpLog extends BaseEntity {



    /**
     * 名称
     */
    private String name;



    /**
     * 是否执行成功（Y-是，N-否）
     */
    private String success;

    /**
     * 具体消息
     */
    @Lob
    private String message;

    /**
     * ip
     */
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
     * 类名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 请求方式（GET POST PUT DELETE)
     */
    private String reqMethod;

    /**
     * 请求参数
     */
    @Lob
    private String param;

    /**
     * 返回结果
     */
    @Lob
    private String result;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime opTime;

    /**
     * 操作人
     */
    private String account;
}
