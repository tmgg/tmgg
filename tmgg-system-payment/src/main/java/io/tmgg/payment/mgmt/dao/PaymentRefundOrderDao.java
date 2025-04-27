package io.tmgg.payment.mgmt.dao;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRefundOrderDao extends BaseDao<PaymentRefundOrder> {

    public PaymentRefundOrder findByOutRefundNo(String outRefundNo) {
        JpaQuery<PaymentRefundOrder> q = new JpaQuery<>();
        q.eq(PaymentRefundOrder.Fields.outRefundNo, outRefundNo);
        return this.findOne(q);
    }
}

