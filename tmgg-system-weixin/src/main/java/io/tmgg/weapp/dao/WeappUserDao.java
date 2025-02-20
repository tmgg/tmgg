package io.tmgg.weapp.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.weapp.entity.WeappUser;
import org.springframework.stereotype.Repository;

@Repository
public class WeappUserDao extends BaseDao<WeappUser> {

    public WeappUser findByOpenId(String openId) {
        JpaQuery<WeappUser> q = new JpaQuery<>();
        q.eq(WeappUser.Fields.openId, openId);

        return this.findOne(q);
    }
}
