package io.tmgg.weixin.entity;

import io.tmgg.lang.ann.Msg;
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

    String token;

    String encodingAESKey;

}
