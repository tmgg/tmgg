package io.tmgg.modules.sys.service;

import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.dao.SysUserMessageDao;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.entity.SysUserMessage;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
public class SysUserMessageService extends BaseService<SysUserMessage> {

    @Resource
    SysUserMessageDao sysUserMsgDao;

    public Page<SysUserMessage> findByUser(String id, Boolean read, Pageable pageable) {
        JpaQuery<SysUserMessage> q = new JpaQuery<>();
        q.eq(SysUserMessage.Fields.user + ".id", id);
        if(read != null){
            q.eq(SysUserMessage.Fields.isRead , read);
        }

        Page<SysUserMessage> list = sysUserMsgDao.findAll(q, pageable);
        return list;
    }

    public long countUnReadByUser(String id) {
        JpaQuery<SysUserMessage> q = new JpaQuery<>();
        q.eq(SysUserMessage.Fields.user + ".id", id);
            q.eq(SysUserMessage.Fields.isRead , false);

       return sysUserMsgDao.count(q);
    }

    @Transactional
    public void save(String userId, String title, String content){
        SysUserMessage msg = new SysUserMessage();
        msg.setUser(new SysUser(userId));
        msg.setTitle(title);
        msg.setContent(content);
        sysUserMsgDao.save(msg);
    }

    public void read(String id) {
        SysUserMessage db = sysUserMsgDao.findOne(id);
        db.setReadTime(new Date());
        db.setIsRead(true);
        sysUserMsgDao.save(db);
    }
}
