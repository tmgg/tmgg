package io.tmgg.payment.mgmt.dao;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentOrderDao extends BaseDao<PaymentOrder> {

    public PaymentOrder findByOutTradeNo(String outTradeNo) {
        JpaQuery<PaymentOrder> q = new JpaQuery<>();
        q.eq(PaymentOrder.Fields.outTradeNo, outTradeNo);
        return this.findOne(q);
    }
}

