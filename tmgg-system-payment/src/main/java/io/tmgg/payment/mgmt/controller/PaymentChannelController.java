package io.tmgg.payment.mgmt.controller;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.payment.IPaymentMethodService;
import io.tmgg.payment.PaymentService;
import io.tmgg.payment.mgmt.entity.PaymentChannel;
import io.tmgg.payment.mgmt.service.PaymentChannelService;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.CommonQueryParam;


import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<PaymentChannel> q = new JpaQuery<>();

        // 关键字搜索，请补全字段
        q.searchText(searchText, PaymentChannel.Fields.remark, PaymentChannel.Fields.appId, PaymentChannel.Fields.mchId);

        Page<PaymentChannel> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }


    @GetMapping("paymentMethodOptions")
    public AjaxResult paymentMethodOptions() throws Exception {
        Collection<IPaymentMethodService> list = paymentService.getPaymentMethodList();

        List<Option> options = Option.convertList(list, IPaymentMethodService::getCode, IPaymentMethodService::getName);

        return AjaxResult.ok().data(options);
    }

}

