package io.tmgg.weapp.entity;

import io.tmgg.lang.ann.Remark;
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
    @Remark("应用id")
    @Column(length = 32, unique = true)
    String appId;

    @NotNull
    @Remark("密钥")
    @Column(length = 32)
    String appSecret;


    @NotNull
    @Remark("备注")
    String remark;

}
