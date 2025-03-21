package io.tmgg.payment.mgmt.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.payment.IPaymentMethodService;
import io.tmgg.payment.PaymentService;
import io.tmgg.payment.mgmt.dao.PaymentChannelDao;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class PaymentChannelService extends BaseService<PaymentChannel> implements CommandLineRunner {

    @Resource
    private PaymentChannelDao paymentChannelDao;

    @Resource
    private PaymentService paymentService;

    @Override
    public PaymentChannel saveOrUpdate(PaymentChannel input) throws Exception {
        String paymentMethod = input.getMethodCode();
        IPaymentMethodService m = paymentService.getPaymentMethod(paymentMethod);
        input.setMethodName(m.getName());
        input.setMethodCode(m.getCode());
        input.setMethodOffline(m.isOffline());
        input.setMethodDisplayName(m.getDisplayName());
        PaymentChannel channel = super.saveOrUpdate(input);
        this.initConfig();
        return channel;
    }

    public List<PaymentChannel> findAllEnable() {
        JpaQuery<PaymentChannel> q = new JpaQuery<>();
        q.eq(PaymentChannel.Fields.enable, true);
        return paymentChannelDao.findAll(q, Sort.by("seq"));
    }


    // 系统启动时, 初始化各支付组件
    @Override
    public void run(String... args) throws Exception {
        initConfig();
    }

    private void initConfig() throws Exception {
        Collection<IPaymentMethodService> list = paymentService.getPaymentMethodList();
        List<PaymentChannel> channelList = this.findAllEnable();
        for (IPaymentMethodService paymentMethodService : list) {
            paymentMethodService.init(channelList);
        }
    }
}
