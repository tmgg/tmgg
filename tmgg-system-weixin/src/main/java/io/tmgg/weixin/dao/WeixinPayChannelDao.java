package io.tmgg.weixin.dao;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.weixin.entity.WeixinPayChannel;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WeixinPayChannelDao extends BaseDao<WeixinPayChannel> {

    public List<WeixinPayChannel> findEnableList() {
        JpaQuery<WeixinPayChannel> q = new JpaQuery<>();
        q.eq(WeixinPayChannel.Fields.enable, true);

        return this.findAll(q);
    }
}

