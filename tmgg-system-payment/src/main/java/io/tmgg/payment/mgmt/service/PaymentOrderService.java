package io.tmgg.payment.mgmt.service;

import io.tmgg.web.persistence.BaseService;
import io.tmgg.payment.PaymentService;
import io.tmgg.payment.enums.PaymentStatus;
import io.tmgg.payment.mgmt.dao.PaymentOrderDao;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderService extends BaseService<PaymentOrder> {

    @Resource
    private PaymentOrderDao paymentOrderDao;

    @Resource
    private PaymentService paymentService;

    public PaymentOrder findByOutTradeNo(String outTradeNo) {
        return paymentOrderDao.findByOutTradeNo(outTradeNo);
    }


    public void queryOrder(String id) throws Exception {
        PaymentOrder order = paymentOrderDao.findOne(id);
        PaymentStatus paymentStatus = paymentService.queryOrder(order);
        order.setStatus(paymentStatus);
        paymentOrderDao.save(order);
    }

    public void triggerNotify(String id) {
        PaymentOrder order = paymentOrderDao.findOne(id);
        paymentService.triggerNotify(order);
    }

    public void refund(String id) throws Exception {
        PaymentOrder order = paymentOrderDao.findOne(id);
        paymentService.refund(order.getOutTradeNo());
    }
}

