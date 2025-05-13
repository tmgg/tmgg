
package io.tmgg.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.persistence.BaseEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.experimental.FieldNameConstants;

import java.util.Date;


@Getter
@Setter
@Entity
@FieldNameConstants
public class SysUserMessage extends BaseEntity {


    @Column(length = 50)
    private String title;


    @Column(length = 10000)
    private String content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private SysUser user;


    @Column
    private Boolean isRead;

    private Date readTime;


}
