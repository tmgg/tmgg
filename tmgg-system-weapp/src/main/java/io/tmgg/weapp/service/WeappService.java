package io.tmgg.weapp.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.weapp.entity.WeixinMiniapp;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class WeappService extends BaseService<WeixinMiniapp> {

    @Resource
    WxMaService wxMaService;



    @Override
    public WeixinMiniapp saveOrUpdate(WeixinMiniapp input) throws Exception {
        WeixinMiniapp weixinMiniapp = super.saveOrUpdate(input);
        this.refresh();
        return weixinMiniapp;
    }

    @PostConstruct
    public void init(){
        this.refresh();
    }


    public void refresh(){
        List<WeixinMiniapp> list = this.findAll();

        for (WeixinMiniapp weixinMiniapp : list) {
            WxMaDefaultConfigImpl cfg = new WxMaDefaultConfigImpl();

            String appId = weixinMiniapp.getAppId();
            cfg.setAppid(appId);
            cfg.setSecret(weixinMiniapp.getAppSecret());

            wxMaService.addConfig(appId, cfg);
        }
    }



}
