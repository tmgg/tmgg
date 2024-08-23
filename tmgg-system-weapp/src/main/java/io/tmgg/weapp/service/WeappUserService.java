package io.tmgg.weapp.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.weapp.dao.WeappUserDao;
import io.tmgg.weapp.entity.WeappUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Service
public class WeappUserService extends BaseService<WeappUser> {

    @Resource
    private WeappUserDao weappUserDao;


    public WeappUser findByOpenId(String openId) {
        return weappUserDao.findByOpenId(openId);
    }

    @Transactional
    public WeappUser updateNickName(String id, WeappUser user) {
        WeappUser db = weappUserDao.findOne(id);
        if (StringUtils.isNotBlank(user.getNickName())) {
            db.setNickName(user.getNickName());
        }


        return db;
    }

    @Transactional
    public WeappUser updateAvatar(String id, String png) {
        WeappUser db = weappUserDao.findOne(id);
        if (StringUtils.isNotBlank(png)) {
            db.setAvatarUrl(png);
        }
        return db;
    }
}
