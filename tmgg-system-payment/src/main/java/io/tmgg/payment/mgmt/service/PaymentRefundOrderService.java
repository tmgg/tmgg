package io.tmgg.payment.mgmt.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.payment.PaymentService;
import io.tmgg.payment.enums.PaymentRefundStatus;
import io.tmgg.payment.mgmt.dao.PaymentRefundOrderDao;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PaymentRefundOrderService extends BaseService<PaymentRefundOrder> {

    @Resource
    private PaymentRefundOrderDao paymentRefundOrderDao;

    @Resource
    private PaymentService paymentService;

    public PaymentRefundOrder findByOutRefundNo(String outRefundNo) {
        return paymentRefundOrderDao.findByOutRefundNo(outRefundNo);
    }

    public void queryOrder(String id) throws Exception {
        PaymentRefundOrder order = paymentRefundOrderDao.findOne(id);
        PaymentRefundStatus paymentStatus = paymentService.queryRefundOrder(order);
        order.setStatus(paymentStatus);
        paymentRefundOrderDao.save(order);
    }

    public void triggerNotify(String id) {
        PaymentRefundOrder order = paymentRefundOrderDao.findOne(id);
        paymentService.triggerRefundNotify(order);
    }
}

