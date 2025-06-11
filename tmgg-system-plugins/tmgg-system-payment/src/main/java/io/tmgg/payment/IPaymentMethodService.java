package io.tmgg.payment;

import io.tmgg.payment.mgmt.entity.PaymentChannel;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.payment.enums.PaymentRefundStatus;
import io.tmgg.payment.enums.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPaymentMethodService {


    /**
     * 需要的，用来区分
     * payment	alipay	支付宝支付
     * wxpay	微信支付
     * baidu	百度收银台
     * appleiap	苹果应用内支付	iOS 应用打包后可获取
     * google-pay	Google Pay支付	App 3.3.7+，Android 应用打包后可获取，Android 设备装有 18.0.0 或更高版本的 Google Play 服务
     * paypal	PayPal支付	App 3.3.7+，iOS11.0+支持，Android 5.0+ (API21+)
     * stripe	Stripe支付
     * https://uniapp.dcloud.net.cn/api/plugins/provider.html#getprovider
     *
     * @return
     */
    String getCode();
    /**
     * 内部名称
     * @return
     */
    String getName();

    /**
     * 外部名称
     * @return
     */
    String getDisplayName();



    /**
     * 是否线下支付
     *
     * @return
     */
    default boolean isOffline() {
        return false;
    }

    /**
     * 初始化， 支持多渠道， 例如商城多店铺情况，可能每个商户需要独立支付
     */
    void init(List<PaymentChannel> channelConfigList) throws Exception;


    Object createOrder(PaymentOrder paymentOrder,String notifyUrl) throws Exception;


    PaymentRefundStatus refund(PaymentRefundOrder paymentRefundOrder,String notifyUrl) throws Exception;


    PaymentStatus parsePayNotify(String notifyData, HttpServletRequest request) throws Exception;

    PaymentRefundStatus parseRefundNotify(String notifyData, HttpServletRequest request) throws Exception;

    ResponseEntity<String> responsePayNotify(boolean success, String msg) throws Exception;



    PaymentStatus queryOrderStatus(PaymentOrder order) throws Exception;

    PaymentRefundStatus queryRefundOrderStatus(PaymentRefundOrder order) throws Exception;
}
