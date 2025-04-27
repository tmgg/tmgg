package io.tmgg.weixin.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Msg("公众号")
@Entity
@Getter
@Setter
public class WeixinMp extends BaseEntity {

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


    String token;

    String encodingAESKey;

}
