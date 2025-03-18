package io.tmgg.weixin.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.SecureUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import io.tmgg.lang.HexTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.weixin.dao.WeixinPayChannelDao;
import io.tmgg.weixin.entity.WeixinPayChannel;
import io.tmgg.lang.dao.BaseService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class WeixinPayChannelService extends BaseService<WeixinPayChannel> {
    private TimedCache<String, byte[]> fileCache = CacheUtil.newTimedCache(1000 * 60 * 10);

    @Resource
    private WeixinPayChannelDao weixinPayChannelDao;

    @Resource
    private WxPayService wxPayService;

    @PostConstruct
    public void init() {
        this.loadConfig();
    }


    public String cacheFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        String md5 = SecureUtil.md5(new ByteArrayInputStream(bytes));

        fileCache.put(md5, bytes);
        return md5;
    }


    @Override
    public WeixinPayChannel saveOrUpdate(WeixinPayChannel input) throws Exception {
        boolean isNew = input.isNew();
        String fileId = input.getKeyContentMd5();
        if (isNew) {
            byte[] keyContent = fileCache.get(fileId);
            input.setKeyContent(keyContent);
            return baseDao.save(input);
        }

        WeixinPayChannel old = baseDao.findOne(input);
       byte[] oldKeyContent = old.getKeyContent();

        BeanUtil.copyProperties(input, old, CopyOptions.create().setIgnoreProperties(BaseEntity.BASE_ENTITY_FIELDS));

        // 文件id一样，表示内容没变
        if (old.getKeyContentMd5().equals(fileId)) {
            old.setKeyContent(oldKeyContent);
        }


        WeixinPayChannel weixinPayChannel = baseDao.save(old);

        this.loadConfig();
        return weixinPayChannel;
    }


    public void loadConfig() {
        List<WeixinPayChannel> list = this.findAll();

        for (WeixinPayChannel item : list) {
            WxPayConfig payConfig = new WxPayConfig();
            payConfig.setAppId(item.getAppId());
            payConfig.setMchId(item.getMchId());
            payConfig.setMchKey(item.getMchKey());
            payConfig.setKeyContent(item.getKeyContent());

            wxPayService.setConfig(payConfig);
        }
    }
}

