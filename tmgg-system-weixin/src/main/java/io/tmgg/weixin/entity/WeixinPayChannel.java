package io.tmgg.weixin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("支付渠道")
@Entity
@Getter
@Setter
@FieldNameConstants
public class WeixinPayChannel extends BaseEntity {


    @Msg("名称")
    private String name;


    /**
     * 微信公众号或者小程序等的appid
     */
    private String appId;

    /**
     * 微信支付商户号
     */
    @Msg("商户号")
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    @Msg("商户密钥")
    private String mchKey;


    /**
     * .p12文件, hex编码后
     */
    @NotNull
    @JsonIgnore
    @Msg("p12文件")
    @Lob
    @Column(columnDefinition = DBConstants.TYPE_BLOB)
    private byte[] keyContent;

    private String keyContentMd5;


}
