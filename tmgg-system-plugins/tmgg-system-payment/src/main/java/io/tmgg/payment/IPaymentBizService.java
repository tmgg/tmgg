package io.tmgg.payment;

import io.tmgg.payment.enums.PaymentRefundStatus;
import io.tmgg.payment.enums.PaymentStatus;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * 支付业务，
 * 由具体业务实现，如下单，充值等
 */
public interface IPaymentBizService {

    /**
     * 业务标识， 如商品订单， 充值等
     * @return
     */
    String getBizCode();

    PaymentBizInfo createOrder(String id) ;


    /**
     * 注意 可能会重复触发
     * @param channel
     * @param status
     * @param outTradeNo
     * @param amt
     */
    void onPayNotify(PaymentChannel channel, String outTradeNo, PaymentStatus status, BigDecimal amt);

    /**
     * 注意 可能会重复触发
     * @param channel
     * @param status
     * @param outTradeNo
     * @param amt
     */
    void onRefundNotify(PaymentChannel channel, String outTradeNo, PaymentRefundStatus status, BigDecimal amt);


    /**
     * 支持的支付渠道， 默认所有
     *
     * @return 指定的渠道ID
     */
    default Collection<String> getCustomChannelList() {
        return null;
    }




    @Data
    class PaymentBizInfo {

        // 微信用户
        private String openId;

        /**
         * 商户订单号
         */
        private String outTradeNo;


        /**
         * 金额（分）
         */
        private int amount;


        private String description;

    }
}
