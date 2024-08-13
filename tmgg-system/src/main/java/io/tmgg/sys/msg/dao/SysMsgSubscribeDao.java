
package io.tmgg.sys.msg.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.sys.msg.entity.SysMsgSubscribe;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SysMsgSubscribeDao extends BaseDao<SysMsgSubscribe>  {

    public List<String> findUserListByTopic(String topic){
        List<SysMsgSubscribe> list = this.findAllByField(SysMsgSubscribe.Fields.topic, topic);

        return list.stream().map(SysMsgSubscribe::getSysUserId).collect(Collectors.toList());
    }

    public List<String> findTopicListByUserId(String userId) {
        List<SysMsgSubscribe> list = this.findAllByField(SysMsgSubscribe.Fields.sysUserId, userId);
        return list.stream().map(SysMsgSubscribe::getTopic).collect(Collectors.toList());
    }

    public SysMsgSubscribe findByUserAndTopic(String userId, String topic) {
        SysMsgSubscribe db = this.findOneByField(SysMsgSubscribe.Fields.topic, topic, SysMsgSubscribe.Fields.sysUserId, userId);
        return db;
    }
}
