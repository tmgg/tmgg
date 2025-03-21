package io.tmgg.payment.mgmt.dao;

import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentOrderDao extends BaseDao<PaymentOrder> {

    public PaymentOrder findByOutTradeNo(String outTradeNo) {
        return this.findOneByField(PaymentOrder.Fields.outTradeNo, outTradeNo);
    }
}

