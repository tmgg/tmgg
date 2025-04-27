
package io.tmgg.payment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import io.tmgg.lang.AmtTool;
import io.tmgg.lang.ExceptionToMessageTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.payment.enums.PaymentRefundStatus;
import io.tmgg.payment.enums.PaymentStatus;
import io.tmgg.payment.mgmt.service.PaymentChannelService;
import io.tmgg.payment.mgmt.service.PaymentOrderService;
import io.tmgg.payment.mgmt.service.PaymentRefundOrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.tmgg.payment.PaymentService.PAY_NOTIFY_URL;
import static io.tmgg.payment.PaymentService.REFUND_NOTIFY_URL;

@Slf4j
public abstract   class PaymentController {




    @Resource
    private PaymentChannelService paymentChannelService;


    @Resource
    private PaymentOrderService paymentOrderService;


    @Resource
    private PaymentRefundOrderService paymentRefundOrderService;

    @Resource
    private PaymentService paymentService;

    @GetMapping("channelList")
    public AjaxResult channelList(@RequestParam String bizCode) {
        IPaymentBizService bizService = paymentService.getBizService(bizCode);
        Collection<String> customChannelList = bizService.getCustomChannelList();

        List<PaymentChannel> channelList = paymentChannelService.findAllEnable();

        channelList = channelList.stream().filter(t->!t.getMethodOffline()).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(customChannelList)) {
            channelList = channelList.stream().filter(t -> customChannelList.contains(t.getId())).collect(Collectors.toList());
        }



        List<Dict> rsList = new ArrayList<>();
        for (PaymentChannel channel : channelList) {
            Dict item = new Dict();

            item.put("name", channel.getMethodDisplayName());
            item.put("id", channel.getId());
            item.put("icon", channel.getIcon());
            rsList.add(item);
        }


        return AjaxResult.ok().data(rsList);
    }

    @GetMapping("previewOrder")
    public AjaxResult previewOrder(@RequestParam String bizCode, String orderId, HttpServletRequest req) throws Exception {
        IPaymentBizService paymentBizService = paymentService.getBizService(bizCode);

        PaymentOrder paymentOrder = new PaymentOrder();
        IPaymentBizService.PaymentBizInfo info = paymentBizService.createOrder(orderId);
        paymentOrder.setOpenId(info.getOpenId());
        paymentOrder.setAmount(info.getAmount());
        paymentOrder.setDescription(info.getDescription());
        paymentOrder.setOutTradeNo(info.getOutTradeNo());


        return AjaxResult.ok().data(paymentOrder);
    }


    @GetMapping("createOrder")
    public AjaxResult createOrder(@RequestParam String bizCode, String channelId, String orderId, HttpServletRequest req) throws Exception {
        Object result=   paymentService.createOrder(bizCode, channelId, orderId);

        return AjaxResult.ok().data(result);
    }


    @RequestMapping( PAY_NOTIFY_URL)
    public ResponseEntity<String> payNotify(@PathVariable String outTradeNo, @RequestBody String notifyData, HttpServletRequest req) throws Exception {
        PaymentOrder paymentOrder = paymentOrderService.findByOutTradeNo(outTradeNo);
        PaymentChannel channel = paymentOrder.getChannel();
        String bizCode = paymentOrder.getBizCode();
        IPaymentMethodService paymentMethodService = paymentService.getPaymentMethod(channel.getMethodCode());
        IPaymentBizService paymentBizService = paymentService.getBizService(bizCode);

        try {
            log.info("【支付回调通知处理】:{}", notifyData);
            PaymentStatus status = paymentMethodService.parsePayNotify(notifyData, req);
            paymentOrder.setStatus(status);

            paymentBizService.onPayNotify(channel, outTradeNo, status, AmtTool.fenToYuan(paymentOrder.getAmount()) );
            return paymentMethodService.responsePayNotify(true, "");
        } catch (Exception e) {
            return paymentMethodService.responsePayNotify(false, e.getMessage());
        }
    }

    @RequestMapping( REFUND_NOTIFY_URL)
    public ResponseEntity<String> refundNotify(@PathVariable String outRefundNo, @RequestBody String notifyData, HttpServletRequest req) throws Exception {
        PaymentRefundOrder paymentRefundOrder = paymentRefundOrderService.findByOutRefundNo(outRefundNo);
        String outTradeNo = paymentRefundOrder.getOutTradeNo();

        PaymentOrder paymentOrder = paymentOrderService.findByOutTradeNo(outTradeNo);
        PaymentChannel channel = paymentOrder.getChannel();
        String bizCode = paymentOrder.getBizCode();

        IPaymentMethodService methodService = paymentService.getPaymentMethod(channel.getMethodCode());
        IPaymentBizService paymentBizService = paymentService.getBizService(bizCode);

        try {
            log.info("【退款回调通知处理】:{}", notifyData);
            PaymentRefundStatus status = methodService.parseRefundNotify(notifyData, req);


            paymentBizService.onRefundNotify(channel, outTradeNo, status, AmtTool.fenToYuan( paymentOrder.getAmount()));
            return methodService.responsePayNotify(true, "");
        } catch (Exception e) {
            return methodService.responsePayNotify(false, e.getMessage());
        }
    }




    @ExceptionHandler(Throwable.class)
    public AjaxResult exception(Throwable e) {
        e.printStackTrace();
        return AjaxResult.err().msg(ExceptionToMessageTool.convert(e));

    }
}
