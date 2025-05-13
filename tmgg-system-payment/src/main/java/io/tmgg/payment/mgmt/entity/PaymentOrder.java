package io.tmgg.payment.mgmt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.ann.Msg;
import io.tmgg.persistence.BaseEntity;
import io.tmgg.payment.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;


/***
 * 支付业务类创建订单是
 */
@Msg("支付订单")
@Getter
@Setter
@Entity
@FieldNameConstants
public class PaymentOrder extends BaseEntity {


    @Msg("微信用户")
    private String openId;

    @Column(unique = true)
    @Msg("商户订单号")
    private String outTradeNo;


    @Min(1)
    @Msg("金额（分）")
    private int amount;

    @Msg("描述")
    @NotNull
    private String description;



    @JsonIgnore
    @ManyToOne
    private PaymentChannel channel;





    @Msg("状态")
    private PaymentStatus status;


    @NotNull
    @Msg("业务编码")
    private String bizCode;



    // 冗余字段
    private String methodDisplayName;
    private String methodName;
    private String methodCode;
}
