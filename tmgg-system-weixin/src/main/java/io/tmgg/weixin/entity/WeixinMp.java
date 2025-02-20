package io.tmgg.weixin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WeixinMp extends BaseEntity {


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
