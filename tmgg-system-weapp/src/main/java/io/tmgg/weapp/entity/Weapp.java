package io.tmgg.weapp.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "sys_weapp")
public class Weapp extends BaseEntity {


    @NotNull
    @Msg("应用id")
    @Column(length = 32, unique = true)
    String appId;

    @NotNull
    @Msg("密钥")
    @Column(length = 32)
    String appSecret;


    @NotNull
    @Msg("备注")
    String remark;

}
