package io.tmgg.weapp.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
