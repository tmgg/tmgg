
package io.tmgg.sys.msg.entity;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * @author 姜涛
 */
@Getter
@Setter
@Entity
public class SysMsgUser extends BaseEntity {


    @Column(nullable = false)
    private String msgId;

    @Column(nullable = false)
    private String userId;


    @Column
    private Boolean isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;


}
