package io.tmgg.payment.impl;

import io.tmgg.payment.IPaymentMethodService;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.payment.enums.PaymentRefundStatus;
import io.tmgg.payment.enums.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public  class PaymentMethodServiceOfflineImpl implements IPaymentMethodService {


    @Override
    public String getCode() {
        return "offlinePay";
    }

    @Override
    public String getName() {
        return "统一线下支付";
    }
    @Override
    public String getDisplayName() {
        return "统一线下支付";
    }

    @Override
    public void init(List<PaymentChannel> channelConfigList) throws Exception {

    }

    @Override
    public Object createOrder(PaymentOrder paymentOrder, String notifyUrl) throws Exception {
        return null;
    }

    @Override
    public PaymentRefundStatus refund(PaymentRefundOrder paymentRefundOrder, String notifyUrl) throws Exception {
        return null;
    }

    @Override
    public PaymentStatus parsePayNotify(String notifyData, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public PaymentRefundStatus parseRefundNotify(String notifyData, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseEntity<String> responsePayNotify(boolean success, String msg) throws Exception {
        return null;
    }

    @Override
    public PaymentStatus queryOrderStatus(PaymentOrder order) throws Exception {
        return null;
    }

    @Override
    public PaymentRefundStatus queryRefundOrderStatus(PaymentRefundOrder order) throws Exception {
        return null;
    }






}
