package io.tmgg.payment.impl;

import cn.hutool.core.codec.Base64;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Response;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import io.tmgg.payment.IPaymentMethodService;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.payment.enums.PaymentRefundStatus;
import io.tmgg.payment.enums.PaymentStatus;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class PaymentMethodServiceWeixinV3Impl implements IPaymentMethodService {


    @Resource
    private WxPayService wxPayService;

    @Override
    public String getCode() {
        return "wxpay";
    }

    @Override
    public String getDisplayName() {
        return "微信支付";
    }

    @Override
    public String getName() {
        return "微信支付（小程序、公众号）";
    }

    @Override
    public void init(List<PaymentChannel> paymentChannelList) throws Exception {
        for (PaymentChannel item : paymentChannelList) {
            WxPayConfig payConfig = new WxPayConfig();
            String keyContent = item.getP12FileBase64();

            payConfig.setAppId(item.getAppId());
            payConfig.setMchId(item.getMchId());
            payConfig.setMchKey(item.getMchKey());
            payConfig.setApiV3Key(item.getMchKey());
            payConfig.setKeyContent(Base64.decode(keyContent));

            wxPayService.addConfig(item.getMchId(), payConfig);
        }
    }


    @Override
    public Object createOrder(PaymentOrder order,String notifyUrl) throws Exception {
        WxPayConfig config = wxPayService.getConfig();
        config.setApiV3Key(config.getMchKey());


        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(order.getAmount());
        amount.setCurrency("CNY");
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(order.getOpenId());


        WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
        request.setDescription(order.getDescription());
        request.setOutTradeNo(order.getOutTradeNo());
        request.setAmount(amount);
        request.setPayer(payer);
        request.setNotifyUrl(notifyUrl);


        Object result = wxPayService.createOrderV3(TradeTypeEnum.JSAPI, request);


        log.info("创建支付订单结果 {}", result);

        return result;
    }


    @Override
    public PaymentRefundStatus refund(PaymentRefundOrder order,String notifyUrl) throws Exception {
        WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
        int refundAmt = order.getAmount();
        String outTradeNo = order.getOutTradeNo();
        String outRefundNo = order.getOutRefundNo();

        amount.setRefund(refundAmt);
        amount.setTotal(refundAmt);
        amount.setCurrency("CNY");
        WxPayRefundV3Request request = new WxPayRefundV3Request();

        request.setOutTradeNo(outTradeNo);
        request.setAmount(amount);
        request.setOutRefundNo(outRefundNo);
        request.setNotifyUrl(notifyUrl);

        WxPayRefundV3Result result = wxPayService.refundV3(request);
        String status = result.getStatus();
        return PaymentRefundStatus.valueOf(status);
    }



    @Override
    public PaymentStatus parsePayNotify(String notifyData, HttpServletRequest request) throws Exception {
        SignatureHeader header = getRequestHeader(request);

        WxPayNotifyV3Result res = this.wxPayService.parseOrderNotifyV3Result(notifyData, header);
        WxPayNotifyV3Result.DecryptNotifyResult decryptRes = res.getResult();

        String tradeState = decryptRes.getTradeState();
        PaymentStatus status = PaymentStatus.valueOf(tradeState);


        return status;
    }

    @Override
    public PaymentRefundStatus parseRefundNotify(String notifyData, HttpServletRequest request) throws Exception {
        SignatureHeader header = getRequestHeader(request);
        WxPayRefundNotifyV3Result res = this.wxPayService.parseRefundNotifyV3Result(notifyData, header);
        WxPayRefundNotifyV3Result.DecryptNotifyResult rs = res.getResult();


        String refundStatus = rs.getRefundStatus();
        PaymentRefundStatus status = PaymentRefundStatus.valueOf(refundStatus);

        return status;
    }

    @Override
    public PaymentStatus queryOrderStatus(PaymentOrder order) throws Exception {
        WxPayOrderQueryV3Result result = wxPayService.queryOrderV3(null, order.getOutTradeNo());
        String tradeState = result.getTradeState();
        return PaymentStatus.valueOf(tradeState);
    }

    @Override
    public PaymentRefundStatus queryRefundOrderStatus(PaymentRefundOrder order) throws Exception {
        WxPayRefundQueryV3Result result = wxPayService.refundQueryV3(order.getOutRefundNo());
        String tradeState = result.getStatus();
        return PaymentRefundStatus.valueOf(tradeState);
    }



    @Override
    public ResponseEntity<String> responsePayNotify(boolean success, String msg) throws Exception {
        if (success) {
            //成功返回200/204，body无需有内容
            return ResponseEntity.status(200).body("");
        } else {
            //失败返回4xx或5xx，且需要构造body信息
            return ResponseEntity.status(500).body(WxPayNotifyV3Response.fail(msg));
        }
    }


    /**
     * 获取回调请求头：签名相关
     *
     * @param request HttpServletRequest
     * @return SignatureHeader
     */
    public SignatureHeader getRequestHeader(HttpServletRequest request) {
        // 获取通知签名
        String signature = request.getHeader("Wechatpay-Signature");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");

        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(signature);
        signatureHeader.setNonce(nonce);
        signatureHeader.setSerial(serial);
        signatureHeader.setTimeStamp(timestamp);
        return signatureHeader;
    }


}
