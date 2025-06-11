package io.tmgg.weixin.config;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WeixinConfig {




    @Bean
    public cn.binarywang.wx.miniapp.api.WxMaService wxMaService() {
        cn.binarywang.wx.miniapp.api.WxMaService wxMaService = new WxMaServiceImpl();
        return wxMaService;
    }

    @Bean
    public WxPayService WxPayService() {
       return  new WxPayServiceImpl();
    }



}
