package io.tmgg.weixin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Remark("微信小程序")
@Entity
@Getter
@Setter
public class WeixinMini extends BaseEntity {

    @Remark("名称")
    String name;

    @NotNull
    @Remark("应用id")
    @Column(length = 32, unique = true)
    String appId;

    @NotNull
    @Remark("密钥")
    @Column(length = 32)
    String appSecret;


    @Remark("备注")
    String remark;

}
