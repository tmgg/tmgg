package io.tmgg.sys.msg.service;

import io.tmgg.lang.PastTimeFormatTool;
import io.tmgg.lang.jdbc.Jdbc;
import io.tmgg.sys.msg.dao.SysMsgDao;
import io.tmgg.sys.msg.dao.SysMsgUserDao;
import io.tmgg.sys.msg.entity.SysMsg;
import io.tmgg.sys.msg.entity.SysMsgUser;
import io.tmgg.sys.msg.result.MsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
public class SysMsgService {

    @Resource
    SysMsgDao msgDao;

    @Resource
    SysMsgUserDao msgUserDao;

    @Resource
    Jdbc jdbc;


    public void create(Integer msgType, String title, String content, String... userIds) {
        log.info("创建消息 {}", title);
        SysMsg msg = new SysMsg();
        msg.setCreateTime(new Date());
        msg.setTitle(title);
        msg.setContent(content);
        msg.setType(msgType);
        msgDao.save(msg);

        for (String userId : userIds) {
            SysMsgUser msgUser = new SysMsgUser();
            msgUser.setMsgId(msg.getId());
            msgUser.setUserId(userId);
            msgUser.setIsRead(false);
            msgUserDao.save(msgUser);

        }

    }


    public Page<MsgVo> page(Pageable pageable, String userId, Boolean read)  {
        String sql = "select m.title, m.content, m.type, m.link, um.id, um.is_read, um.read_time, um.create_time " +
                "from  sys_msg_user um " +
                "left join sys_msg m on m.id = um.msg_id " +
                "where  um.user_id = ? ";

        Page<MsgVo> page = null;
        if (read == null) {
            page = jdbc.findAll(MsgVo.class, pageable, sql, userId);
        } else {
            sql += "and um.is_read = ?";
            page = jdbc.findAll(MsgVo.class, pageable, sql, userId, read);
        }

        for (MsgVo vo : page.getContent()) {
            vo.setCreateTimeLabel(PastTimeFormatTool.formatPastTime(vo.getCreateTime()));
            vo.setReadTimeLabel(PastTimeFormatTool.formatPastTime(vo.getReadTime()));
        }

        return page;
    }


    // 新消息个数
    public long countNewMsg(String userId) {
        String sql = "select count(*) " +
                "from  sys_msg_user um " +
                "where  um.user_id = ? ";

        sql += "and um.is_read = ?";
        Long scalar = (Long) jdbc.findScalar(sql, userId, false);


        return scalar == null ? 0 : scalar;
    }

    public void read(String id) {
        SysMsgUser db = msgUserDao.findOne(id);
        db.setReadTime(LocalDateTime.now());
        db.setIsRead(true);
        msgUserDao.save(db);
    }
}
