package io.tmgg.weixin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.HexUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.weixin.dao.WeixinPayChannelDao;
import io.tmgg.weixin.entity.WeixinPayChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class WeixinPayChannelService extends BaseService<WeixinPayChannel> {

    @Resource
    private WeixinPayChannelDao weixinPayChannelDao;

    @Resource
    private WxPayService wxPayService;

    @PostConstruct
    public void init() {
        this.loadConfig();
    }


    @Override
    public WeixinPayChannel saveOrUpdate(WeixinPayChannel input) throws Exception {
        boolean isNew = input.isNew();
        if (isNew) {
            this.loadConfig();
            return baseDao.save(input);
        }

        WeixinPayChannel old = baseDao.findOne(input);

        BeanUtil.copyProperties(input, old, CopyOptions.create().setIgnoreProperties(BaseEntity.BASE_ENTITY_FIELDS));

        WeixinPayChannel weixinPayChannel = baseDao.save(old);

        this.loadConfig();
        return weixinPayChannel;
    }

    @Override
    public void deleteById(String id) {
        Assert.hasText(id, "id不能为空");
        WeixinPayChannel db = baseDao.findOne(id);
        baseDao.deleteById(id);
        wxPayService.removeConfig(db.getMchId());
    }

    public void loadConfig() {
        List<WeixinPayChannel> list = weixinPayChannelDao.findAll();

        for (WeixinPayChannel item : list) {
            if (!item.getEnable()) {
                wxPayService.removeConfig(item.getMchId());
                continue;
            }
            WxPayConfig payConfig = new WxPayConfig();
            payConfig.setAppId(item.getAppId());
            payConfig.setMchId(item.getMchId());
            payConfig.setMchKey(item.getMchKey());
            payConfig.setApiV3Key(item.getMchKey());

            String keyContent = item.getKeyContentHex();
            byte[] bytes = HexUtil.decodeHex(keyContent);
            payConfig.setKeyContent(bytes);

            wxPayService.addConfig(item.getMchId(), payConfig);
        }

    }
}

