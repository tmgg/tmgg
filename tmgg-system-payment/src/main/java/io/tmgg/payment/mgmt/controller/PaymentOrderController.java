package io.tmgg.payment.mgmt.controller;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.payment.mgmt.entity.PaymentOrder;
import io.tmgg.payment.mgmt.service.PaymentOrderService;
import io.tmgg.lang.dao.BaseController;
import io.tmgg.web.CommonQueryParam;


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


    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  CommonQueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<PaymentOrder> q = new JpaQuery<>();

        // 关键字搜索，请补全字段
        q.searchText(param.getKeyword(), PaymentOrder.Fields.outTradeNo, PaymentOrder.Fields.description);

        Page<PaymentOrder> page = service.findAll(q, pageable);
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


    @HasPermission(label = "退款")
    @GetMapping("refund")
    public AjaxResult refund(String id) throws Exception {
        service.refund(id);
        return AjaxResult.ok().msg("退款已发起");
    }
}

