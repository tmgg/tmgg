
package io.tmgg.sys.log.entity;

import io.tmgg.lang.dao.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * 系统访问日志表
 *
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysVisLog extends BaseEntity {



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
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visTime;

    /**
     * 访问人
     */
    private String account;
}
