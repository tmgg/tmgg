package io.tmgg.payment.mgmt.dao;

import io.tmgg.data.query.JpaQuery;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.data.repository.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentOrderDao extends BaseDao<PaymentOrder> {

    public PaymentOrder findByOutTradeNo(String outTradeNo) {
        JpaQuery<PaymentOrder> q = new JpaQuery<>();
        q.eq(PaymentOrder.Fields.outTradeNo, outTradeNo);
        return this.findOne(q);
    }
}

