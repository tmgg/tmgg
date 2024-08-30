package io.tmgg.sys.msg;


import cn.hutool.core.util.StrUtil;
import io.tmgg.sys.msg.dao.SysMsgDao;
import io.tmgg.sys.msg.dao.SysMsgSubscribeDao;
import io.tmgg.sys.msg.dao.SysMsgUserDao;
import io.tmgg.sys.msg.entity.SysMsg;
import io.tmgg.sys.msg.entity.SysMsgUser;
import io.tmgg.sys.dao.SysUserDao;
import io.tmgg.sys.entity.SysUser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class MessagePublishService implements IMessagePublishService {

    @Resource
    SysUserDao sysUserDao;


    @Resource
    SysMsgSubscribeDao sysMsgSubscribeDao;

    @Resource
    SysMsgDao sysMsgDao;

    @Resource
    SysMsgUserDao sysMsgUserDao;

    @Resource
    EmailService emailService;

    @Override
    @Async
    public void publish(String topic, String title, String content) {
        List<String> userList = sysMsgSubscribeDao.findUserListByTopic(topic);

        // 系统消息
        SysMsg sysMsg = new SysMsg();
        sysMsg.setTitle(title);
        sysMsg.setTopic(topic);
        sysMsg.setContent(content);
        sysMsg = sysMsgDao.save(sysMsg);
        for (String user : userList) {
            SysMsgUser sysMsgUser = new SysMsgUser();
            sysMsgUser.setUserId(user);
            sysMsgUser.setMsgId(sysMsg.getId());
            sysMsgUser.setIsRead(false);
            sysMsgUserDao.save(sysMsgUser);
        }

        // 发送邮件
        for (String userId : userList) {
            SysUser user = sysUserDao.findOne(userId);
            if(!StrUtil.isEmpty(user.getEmail())){
                emailService.send(user.getEmail(), title, content);
            }
        }

    }

}
