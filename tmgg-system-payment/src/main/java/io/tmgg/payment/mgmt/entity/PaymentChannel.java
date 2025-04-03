package io.tmgg.payment.mgmt.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("支付渠道")
@Getter
@Setter
@Entity
@FieldNameConstants
public class PaymentChannel extends BaseEntity {


    @Msg("图标")
    private String icon;

    @Msg("备注")
    private String remark;

    @Msg("排序")
    private Integer seq;

    @Msg("启用")
    private Boolean enable;


    /**
     * 微信公众号或者小程序等的appid
     */
    @Msg("应用ID")
    private String appId;

    @Msg("商户号")
    private String mchId;

    @Msg("商户密钥")
    private String mchKey;


    /**
     * .p12文件, hex编码后
     */
    @Msg("p12文件")
    @Lob
    @Column(columnDefinition = DBConstants.TYPE_BLOB)
    private String p12FileBase64;


// 以下字段从支付方式IPaymentMethodService实现类获得，方便页面展示

    @Msg("支付方式")
    private String methodCode;

    @Msg("支付方式")
    private String methodName;

    @Msg("支付方式")
    private String methodDisplayName;
    @Msg("是否线下")
    private Boolean methodOffline;
}
