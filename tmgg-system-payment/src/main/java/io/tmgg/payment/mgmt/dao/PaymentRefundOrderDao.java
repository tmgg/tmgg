package io.tmgg.payment.mgmt.dao;

import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRefundOrderDao extends BaseDao<PaymentRefundOrder> {

    public PaymentRefundOrder findByOutRefundNo(String outRefundNo) {
        return this.findOneByField(PaymentRefundOrder.Fields.outRefundNo, outRefundNo);
    }
}

