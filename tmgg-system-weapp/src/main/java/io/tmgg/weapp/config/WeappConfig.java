package io.tmgg.weapp.config;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WeappConfig  {




    @Bean
    public cn.binarywang.wx.miniapp.api.WxMaService wxMaService() {
        cn.binarywang.wx.miniapp.api.WxMaService wxMaService = new WxMaServiceImpl();

        return wxMaService;
    }





}