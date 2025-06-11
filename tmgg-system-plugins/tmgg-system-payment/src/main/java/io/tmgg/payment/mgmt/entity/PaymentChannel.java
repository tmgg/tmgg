package io.tmgg.payment.mgmt.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("支付渠道")
@Getter
@Setter
@Entity
@FieldNameConstants
public class PaymentChannel extends BaseEntity {


    @Remark("图标")
    private String icon;

    @Remark("备注")
    private String remark;

    @Remark("排序")
    private Integer seq;

    @Remark("启用")
    private Boolean enable;


    /**
     * 微信公众号或者小程序等的appid
     */
    @Remark("应用ID")
    private String appId;

    @Remark("商户号")
    private String mchId;

    @Remark("商户密钥")
    private String mchKey;


    /**
     * .p12文件, hex编码后
     */
    @Remark("p12文件")
    @Lob
    @Column(columnDefinition = DBConstants.TYPE_BLOB)
    private String p12FileBase64;


// 以下字段从支付方式IPaymentMethodService实现类获得，方便页面展示

    @Remark("支付方式")
    private String methodCode;

    @Remark("支付方式")
    private String methodName;

    @Remark("支付方式")
    private String methodDisplayName;
    @Remark("是否线下")
    private Boolean methodOffline;
}
