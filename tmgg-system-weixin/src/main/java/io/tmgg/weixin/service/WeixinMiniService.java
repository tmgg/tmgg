package io.tmgg.weixin.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.weixin.entity.WeixinMini;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class WeixinMiniService extends BaseService<WeixinMini> {

    @Resource
    WxMaService wxMaService;



    @Override
    public WeixinMini saveOrUpdate(WeixinMini input) throws Exception {
        WeixinMini weixinMini = super.saveOrUpdate(input);
        this.refresh();
        return weixinMini;
    }

    @PostConstruct
    public void init(){
        this.refresh();
    }


    public void refresh(){
        List<WeixinMini> list = this.findAll();

        for (WeixinMini weixinMini : list) {
            WxMaDefaultConfigImpl cfg = new WxMaDefaultConfigImpl();

            String appId = weixinMini.getAppId();
            cfg.setAppid(appId);
            cfg.setSecret(weixinMini.getAppSecret());

            wxMaService.addConfig(appId, cfg);
        }
    }



}
