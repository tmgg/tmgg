package io.tmgg.payment;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.AmtTool;
import io.tmgg.lang.RequestTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.payment.enums.PaymentStatus;
import io.tmgg.payment.mgmt.dao.PaymentChannelDao;
import io.tmgg.payment.mgmt.dao.PaymentOrderDao;
import io.tmgg.payment.mgmt.dao.PaymentRefundOrderDao;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.payment.enums.PaymentRefundStatus;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PaymentService {







    public static final String PAY_NOTIFY_URL = "/notify/pay/{outTradeNo}";
    public static final String REFUND_NOTIFY_URL = "/notify/refund/{outRefundNo}";

    @Resource
    private PaymentOrderDao paymentOrderDao;

    @Resource
    private PaymentRefundOrderDao paymentRefundOrderDao;

    @Resource
    private PaymentChannelDao paymentChannelDao;

    @Resource
    private SysConfigService sysConfigService;



    @Transactional
    public Object createOrder(String bizCode, String channelId, String orderId) throws Exception {
        IPaymentBizService paymentBizService = getBizService(bizCode);
        PaymentChannel channel = paymentChannelDao.findOne(channelId);
        IPaymentMethodService paymentMethodService = this.getPaymentMethod(channel.getMethodCode());

        PaymentOrder paymentOrder = new PaymentOrder();
        IPaymentBizService.PaymentBizInfo info = paymentBizService.createOrder(orderId);
        paymentOrder.setOpenId(info.getOpenId());
        paymentOrder.setAmount(info.getAmount());
        paymentOrder.setDescription(info.getDescription());
        paymentOrder.setOutTradeNo(info.getOutTradeNo());
        paymentOrder.setChannel(channel);
        paymentOrder.setBizCode(bizCode);

        // 冗余字段
        paymentOrder.setMethodDisplayName(paymentMethodService.getDisplayName());
        paymentOrder.setMethodName(paymentMethodService.getName());
        paymentOrder.setMethodCode(paymentMethodService.getCode());


        Object result = paymentMethodService.createOrder(paymentOrder, this.getNotifyUrlPay(info.getOutTradeNo()));

        log.info("创建支付订单结果 {}", result);
        paymentOrderDao.save(paymentOrder);

        return result;
    }
    public PaymentStatus queryOrder(PaymentOrder order) throws Exception {
        IPaymentMethodService paymentService = this.getPaymentMethod(order.getChannel().getMethodCode());
        return paymentService.queryOrderStatus(order);
    }

    public PaymentRefundStatus queryRefundOrder(PaymentRefundOrder order) throws Exception {
        PaymentOrder paymentOrder = paymentOrderDao.findByOutTradeNo(order.getOutTradeNo());
        IPaymentMethodService paymentService = this.getPaymentMethod(paymentOrder.getChannel().getMethodCode());
        return paymentService.queryRefundOrderStatus(order);
    }

    /**
     * 退全款
     *
     * @return
     */
    public PaymentRefundStatus refund(String outTradeNo) throws Exception {
        PaymentOrder p = paymentOrderDao.findByOutTradeNo(outTradeNo);
        PaymentRefundOrder r = new PaymentRefundOrder();
        r.setOutTradeNo(p.getOutTradeNo());
        r.setOutRefundNo(IdUtil.getSnowflakeNextIdStr());
        r.setAmount(p.getAmount());

        IPaymentMethodService paymentService = this.getPaymentMethod(p.getChannel().getMethodCode());
        String url = this.getNotifyUrlRefund(r.getOutRefundNo());
        PaymentRefundStatus status = paymentService.refund(r, url);


        r.setStatus(status);
        paymentRefundOrderDao.save(r);
        return status;
    }

    public void triggerNotify(PaymentOrder order) {
        PaymentStatus status = order.getStatus();
        IPaymentBizService bizService = this.getBizService(order.getBizCode());
        bizService.onPayNotify(order.getChannel(), order.getOutTradeNo(), status, AmtTool.fenToYuan(order.getAmount()));
    }

    public void triggerRefundNotify(PaymentRefundOrder order) {
        PaymentOrder paymentOrder = paymentOrderDao.findByOutTradeNo(order.getOutTradeNo());
        PaymentRefundStatus status = order.getStatus();
        IPaymentBizService bizService = this.getBizService(paymentOrder.getBizCode());
        bizService.onRefundNotify(paymentOrder.getChannel(), order.getOutTradeNo(), status, AmtTool.fenToYuan(order.getAmount()));
    }

    public IPaymentMethodService getPaymentMethod(String method ) {
        Collection<IPaymentMethodService> beans = SpringTool.getBeans(IPaymentMethodService.class);
        IPaymentMethodService paymentMethodService = beans.stream().filter(t -> t.getCode().equals(method)).findFirst().get();

        return paymentMethodService;
    }

    public Collection<IPaymentMethodService> getPaymentMethodList() {
        Collection<IPaymentMethodService> beans = SpringTool.getBeans(IPaymentMethodService.class);
        return beans;
    }

    public IPaymentBizService getBizService(String bizCode) {
        Collection<IPaymentBizService> paymentBizServiceList = SpringTool.getBeans(IPaymentBizService.class);
        IPaymentBizService paymentBizService = paymentBizServiceList.stream().filter(t -> t.getBizCode().equals(bizCode)).findFirst().get();
        Assert.notNull(paymentBizService, "支付业务类不存在，业务代码" + bizCode);
        return paymentBizService;
    }

    public String getNotifyUrlRefund(String outRefundNo) {
        String baseUrl = getNotifyBaseUrl();
        String suffix = REFUND_NOTIFY_URL.replace("{outRefundNo}", outRefundNo);
        String notifyUrl = joinUrl( baseUrl, urlPrefix() , suffix);
        log.info("退款回调地址:{}", notifyUrl);
        return notifyUrl;
    }

    public String getNotifyUrlPay(String outTradeNo) {
        String baseUrl = getNotifyBaseUrl();
        String suffix = PAY_NOTIFY_URL.replace("{outTradeNo}", outTradeNo);
        String notifyUrl = joinUrl( baseUrl, urlPrefix() , suffix);

        log.info("支付回调地址:{}", notifyUrl);
        return notifyUrl;
    }

    private String joinUrl(String... strs){
        return Arrays.stream(strs).map(s-> StrUtil.removePrefix(s,"/")).map(s->StrUtil.removeSuffix(s,"/")).collect(Collectors.joining("/"));
    }

    private String getNotifyBaseUrl() {
        // 开发调试
        if (ArrayUtil.contains(SpringTool.getActiveProfiles(), "dev")) {
            return "http://103.197.184.184:33725";
        }
        HttpServletRequest request = RequestTool.currentRequest();
        String baseUrl = sysConfigService.getOrParseBaseUrl(request);

        return baseUrl;
    }

    private String _urlPrefix;
    private String urlPrefix(){
        if(_urlPrefix ==null){
            PaymentController controller = SpringTool.getBean(PaymentController.class);
            Assert.notNull(controller, "请定义一个controller，继承自"+ PaymentController.class.getName());
            String[] value = controller.getClass().getAnnotation(RequestMapping.class).value();
            _urlPrefix = value[0];
        }

        return _urlPrefix;
    }

}
