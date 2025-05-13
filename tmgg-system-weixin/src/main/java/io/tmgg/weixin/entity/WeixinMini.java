package io.tmgg.weixin.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Msg("微信小程序")
@Entity
@Getter
@Setter
public class WeixinMini extends BaseEntity {

    @Msg("名称")
    String name;

    @NotNull
    @Msg("应用id")
    @Column(length = 32, unique = true)
    String appId;

    @NotNull
    @Msg("密钥")
    @Column(length = 32)
    String appSecret;


    @Msg("备注")
    String remark;

}
