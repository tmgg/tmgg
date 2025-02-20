package io.tmgg.weixin.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.weixin.entity.WeixinUser;
import org.springframework.stereotype.Repository;

@Repository
public class WeappUserDao extends BaseDao<WeixinUser> {

    public WeixinUser findByOpenId(String openId) {
        JpaQuery<WeixinUser> q = new JpaQuery<>();
        q.eq(WeixinUser.Fields.openId, openId);

        return this.findOne(q);
    }
}
