package io.tmgg.modules.sys.msg.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.sys.msg.dao.SysMsgSubscribeDao;
import io.tmgg.modules.sys.msg.entity.SysMsgSubscribe;
import io.tmgg.modules.sys.msg.entity.SysMsgTopic;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class SysMsgSubscribeService extends BaseService<SysMsgSubscribe> {

    @Resource
    SysMsgSubscribeDao sysMsgSubscribeDao;

    public List<String> findTopicListByUserId(String id) {
        List<String> topicListByUserId = sysMsgSubscribeDao.findTopicListByUserId(id);
        return topicListByUserId;
    }

    public void subscribe(String id, String topic) {
        SysMsgSubscribe s = new SysMsgSubscribe();
        s.setSysUserId(id);
        s.setTopic(topic);
        sysMsgSubscribeDao.save(s);
    }

    public void unSubscribe(String id, String topic) {
        SysMsgSubscribe db = sysMsgSubscribeDao.findByUserAndTopic(id, topic);
        if(db != null){
            sysMsgSubscribeDao.delete(db);
        }
    }
}
