package io.tmgg.payment.mgmt.controller;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.payment.mgmt.service.PaymentOrderService;
import io.tmgg.web.persistence.BaseController;


import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

@RestController
@RequestMapping("paymentOrder")
public class PaymentOrderController  extends BaseController<PaymentOrder>{

    @Resource
    PaymentOrderService service;



    @HasPermission(label = "查询状态")
    @GetMapping("queryOrder")
    public AjaxResult queryOrder(String id) throws Exception {
        service.queryOrder(id);
        return AjaxResult.ok().msg("查询完成");
    }
    @HasPermission(label = "触发通知")
    @GetMapping("triggerNotify")
    public AjaxResult triggerNotify(String id) throws Exception {
        service.triggerNotify(id);
        return AjaxResult.ok().msg("触发完成");
    }


    @HasPermission(label = "退款")
    @GetMapping("refund")
    public AjaxResult refund(String id) throws Exception {
        service.refund(id);
        return AjaxResult.ok().msg("退款已发起");
    }
}

