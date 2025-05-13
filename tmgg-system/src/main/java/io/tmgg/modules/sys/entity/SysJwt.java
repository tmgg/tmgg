package io.tmgg.modules.sys.entity;

import io.tmgg.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
public class SysJwt extends BaseEntity {

    @NotNull
    private Date expireTime;

    /**
     * 为了安全，不直接保存token
     */
    @Column(unique = true)
    @NotNull
    private String tokenMd5;

    @NotNull
    private Boolean disabled;

    private Date disabledTime;
}
