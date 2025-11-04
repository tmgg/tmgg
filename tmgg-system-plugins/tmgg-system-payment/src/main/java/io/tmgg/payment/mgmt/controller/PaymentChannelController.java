package io.tmgg.payment.mgmt.controller;

import io.tmgg.dto.AjaxResult;
import io.tmgg.dto.Option;
import io.tmgg.payment.IPaymentMethodService;
import io.tmgg.payment.PaymentService;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import io.tmgg.payment.mgmt.service.PaymentChannelService;
import io.tmgg.web.persistence.BaseController;


import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("paymentChannel")
public class PaymentChannelController extends BaseController<PaymentChannel> {

    @Resource
    PaymentChannelService service;

    @Resource
    PaymentService paymentService;





    @GetMapping("paymentMethodOptions")
    public AjaxResult paymentMethodOptions() throws Exception {
        Collection<IPaymentMethodService> list = paymentService.getPaymentMethodList();

        List<Option> options = Option.convertList(list, IPaymentMethodService::getCode, IPaymentMethodService::getName);

        return AjaxResult.ok().data(options);
    }

}

