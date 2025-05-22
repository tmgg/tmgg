package io.tmgg.payment.mgmt.dao;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.web.persistence.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRefundOrderDao extends BaseDao<PaymentRefundOrder> {

    public PaymentRefundOrder findByOutRefundNo(String outRefundNo) {
        JpaQuery<PaymentRefundOrder> q = new JpaQuery<>();
        q.eq(PaymentRefundOrder.Fields.outRefundNo, outRefundNo);
        return this.findOne(q);
    }
}

