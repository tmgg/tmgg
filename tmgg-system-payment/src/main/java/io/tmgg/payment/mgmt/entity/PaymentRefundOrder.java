package io.tmgg.payment.mgmt.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.payment.enums.PaymentRefundStatus;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("支付退款")
@Entity
@Getter
@Setter
@FieldNameConstants
public class PaymentRefundOrder extends BaseEntity {

    /**
     * 字段名：商户退款单号
     * 变量名：out_refund_no
     * 是否必填：是
     * 类型：string[1, 64]
     * 描述：
     *  商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     *  示例值：1217752501201407033233368018
     */
    @Msg("商户退款单号")
    private String outRefundNo;

    /**
     * <pre>
     * 字段名：商户订单号
     * 变量名：out_trade_no
     * 是否必填：是
     * 类型：string[1, 32]
     * 描述：
     *  原支付交易对应的商户订单号
     *  示例值：1217752501201407033233368018
     * </pre>
     */
    @Msg("商户订单号")
    private String outTradeNo;


    /**
     * 字段名：退款成功时间
     */
    @Msg("退款成功时间")
    private String successTime;

    @Msg("退款状态")
    private PaymentRefundStatus status;


    @Msg("金额")
    private Integer amount;



}
