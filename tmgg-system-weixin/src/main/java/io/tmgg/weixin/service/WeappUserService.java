package io.tmgg.weixin.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.weixin.dao.WeappUserDao;
import io.tmgg.weixin.entity.WeixinUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Service
public class WeappUserService extends BaseService<WeixinUser> {

    @Resource
    private WeappUserDao weappUserDao;


    public WeixinUser findByOpenId(String openId) {
        return weappUserDao.findByOpenId(openId);
    }

    @Transactional
    public WeixinUser updateNickName(String id, WeixinUser user) {
        WeixinUser db = weappUserDao.findOne(id);
        if (StringUtils.isNotBlank(user.getNickName())) {
            db.setNickName(user.getNickName());
        }


        return db;
    }

    @Transactional
    public WeixinUser updateAvatar(String id, String png) {
        WeixinUser db = weappUserDao.findOne(id);
        if (StringUtils.isNotBlank(png)) {
            db.setAvatarUrl(png);
        }
        return db;
    }
}
