package io.tmgg.payment.mgmt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
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
@Remark("支付订单")
@Getter
@Setter
@Entity
@FieldNameConstants
public class PaymentOrder extends BaseEntity {


    @Remark("微信用户")
    private String openId;

    @Column(unique = true)
    @Remark("商户订单号")
    private String outTradeNo;


    @Min(1)
    @Remark("金额（分）")
    private int amount;

    @Remark("描述")
    @NotNull
    private String description;



    @JsonIgnore
    @ManyToOne
    private PaymentChannel channel;





    @Remark("状态")
    private PaymentStatus status;


    @NotNull
    @Remark("业务编码")
    private String bizCode;



    // 冗余字段
    private String methodDisplayName;
    private String methodName;
    private String methodCode;
}
