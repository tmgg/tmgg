package io.tmgg.weixin.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.weixin.dao.WeixinUserDao;
import io.tmgg.weixin.entity.WeixinUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Service
public class WexinUserService extends BaseService<WeixinUser> {

    @Resource
    private WeixinUserDao weixinUserDao;


    public WeixinUser findByOpenId(String openId) {
        return weixinUserDao.findByOpenId(openId);
    }

    @Transactional
    public WeixinUser updateNickName(String id, WeixinUser user) {
        WeixinUser db = weixinUserDao.findOne(id);

        return db;
    }

    @Transactional
    public WeixinUser updateAvatar(String id, String png) {
        WeixinUser db = weixinUserDao.findOne(id);
        if (StringUtils.isNotBlank(png)) {
            db.setAvatarUrl(png);
        }
        return db;
    }
}
