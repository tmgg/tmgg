package io.tmgg.payment.enums;

public enum PaymentRefundStatus {

    /**
     * 退款成功.
     */
     SUCCESS ,



    /**
     * 退款处理中.
     */
    PROCESSING ,


    /**
     * 退款关闭
     */
     CLOSED,

    /**
     * 退款异常
     */
    ABNORMAL
}
