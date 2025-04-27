package io.tmgg.payment.enums;

public enum PaymentStatus {

    /**
     * 支付成功.
     */
     SUCCESS ,

    /**
     * 支付失败(其他原因，如银行返回失败).
     */
     PAY_ERROR ,

    /**
     * 用户支付中.
     */
     USER_PAYING ,

    /**
     * 已关闭.
     */
     CLOSED,

    /**
     * 未支付.
     */
     NOTPAY,

    /**
     * 转入退款.
     */
     REFUND ,

    /**
     * 已撤销（刷卡支付）.
     */
     REVOKED ,
}
