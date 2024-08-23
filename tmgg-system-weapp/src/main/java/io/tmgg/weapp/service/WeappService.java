package io.tmgg.weapp.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.weapp.entity.Weapp;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class WeappService extends BaseService<Weapp> {

    @Resource
    WxMaService wxMaService;



    @Override
    public Weapp saveOrUpdate(Weapp input) throws Exception {
        Weapp weapp = super.saveOrUpdate(input);
        this.refresh();
        return weapp;
    }

    @PostConstruct
    public void init(){
        this.refresh();
    }


    public void refresh(){
        List<Weapp> list = this.findAll();

        for (Weapp weapp : list) {
            WxMaDefaultConfigImpl cfg = new WxMaDefaultConfigImpl();

            String appId = weapp.getAppId();
            cfg.setAppid(appId);
            cfg.setSecret(weapp.getAppSecret());

            wxMaService.addConfig(appId, cfg);
        }
    }



}
