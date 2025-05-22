package io.tmgg.payment.mgmt.controller;

import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.payment.mgmt.entity.PaymentRefundOrder;
import io.tmgg.payment.mgmt.service.PaymentRefundOrderService;
import io.tmgg.web.persistence.BaseController;


import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

@RestController
@RequestMapping("paymentRefundOrder")
public class PaymentRefundOrderController  extends BaseController<PaymentRefundOrder>{

    @Resource
    PaymentRefundOrderService service;


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(String searchText,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<PaymentRefundOrder> q = new JpaQuery<>();

        q.searchText(searchText, PaymentRefundOrder.Fields.outRefundNo,PaymentRefundOrder.Fields.outTradeNo);

        Page<PaymentRefundOrder> page = service.findAll(q, pageable);
        return AjaxResult.ok().data(page);
    }

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
}

