# 扩展模块 - 支付

app端需没有实例化控制器，需继承 PaymentController, 并设置RequestMapping


示例
```java
package cn.crec.venue.venue.app.controler;

import io.tmgg.payment.PaymentController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("rest/payment")
public class PaymentAppController extends PaymentController {


}


```

前端支付页面，uniapp示例
```
<template>
  <view style="padding:32rpx">
    <u-toast ref="uToast"></u-toast>

    <view style="display: flex;align-items: center;flex-direction: column">
      <view>
        <u--text :text="previewOrder.description"></u--text>
      </view>
      <u-gap></u-gap>
      <view>
        <u--text size="32" mode="price" :text="previewOrder.amount / 100"></u--text>
      </view>
    </view>
    <u-gap></u-gap>   
    <u-radio-group
        v-model="selectedPaymentChannelId"
        placement="column"
        @change="onPaymentChannelChange"
    >
      <u-radio
          :customStyle="{marginBottom: '8px'}"
          v-for="(item, index) in paymentChannelList"
          :key="index"
          :label="item.name"
          :name="item.id"
      >
      </u-radio>
    </u-radio-group>

    <u-gap></u-gap>
    <u-gap></u-gap>
    <u-button @click="callFrontPay" text="确认支付" type="primary"></u-button>

  </view>
</template>

<script>
import http from '@/common/vmeitime-http/interface';

export default {
  data() {
    return {
      orderId: null, // 业务订单id

      // 业务标识， 如订单业务，充值业务等
      bizCode: null,

      // 选择的支付渠道
      selectedPaymentChannelId: null,

	  // 支付成功后返回页面
	  returnPage: '/',

      paymentChannelList: [],

      previewOrder: {
        amount: 0
      },

    }
  },

  onLoad: function (options) {
    this.orderId = options.orderId
    this.bizCode = options.bizCode
	this.returnPage = decodeURIComponent( options.returnPage)
    this.getPreviewOrder()
    this.getPaymentChannelList()
  },
  methods: {
    getPreviewOrder() {
      http.get('/rest/payment/previewOrder', {orderId: this.orderId, bizCode: this.bizCode}).then(response => {
        this.previewOrder = response.data.data
      })
    },

    getPaymentChannelList() {
      http.get('/rest/payment/channelList', {orderId: this.orderId, bizCode: this.bizCode}).then(response => {
        this.paymentChannelList = response.data.data
		if(this.paymentChannelList.length > 0){
			this.selectedPaymentChannelId = this.paymentChannelList[0].id
		}
      })
    },
    onPaymentChannelChange(v) {
      this.selectedPaymentChannelId = v;
    },


    callFrontPay() {
      let params = {orderId: this.orderId, bizCode: this.bizCode, channelId: this.selectedPaymentChannelId};
      http.get('/rest/payment/createOrder', params).then(response => {
        let payInfo = response.data.data
        uni.requestPayment({
          ...payInfo,
          package: payInfo.packageValue,
          success: this.paySuccess,
          fail: this.payFail
        })
      })
    },

    paySuccess(e) {
	  const url = this.returnPage
      this.$refs.uToast.show({
        message: '支付成功',
		complete(){
			uni.navigateTo({
				url:url
			})
		}
      });
    },
    payFail(e) {
      this.$refs.uToast.show({
        message: '支付失败',
      });
    }

  }
}
</script>


```
