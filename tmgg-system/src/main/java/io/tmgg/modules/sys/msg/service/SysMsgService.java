package io.tmgg.modules.sys.msg.service;

import io.tmgg.dbtool.DbTool;
import io.tmgg.lang.PastTimeFormatTool;
import io.tmgg.modules.sys.msg.dao.SysMsgUserDao;
import io.tmgg.modules.sys.msg.entity.SysMsgUser;
import io.tmgg.modules.sys.msg.result.MsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SysMsgService {

    @Resource
    SysMsgUserDao msgUserDao;

    @Resource
    DbTool dbTool;


    public Page<MsgVo> page(Pageable pageable, String userId, Boolean read)  {
        String sql = "select m.title, m.content, m.type, m.link, um.id, um.is_read, um.read_time, um.create_time " +
                "from  sys_msg_user um " +
                "left join sys_msg m on m.id = um.msg_id " +
                "where  um.user_id = ? ";

        Page<MsgVo> page = null;
        if (read == null) {
            page = dbTool.findAll(MsgVo.class, pageable, sql, userId);
        } else {
            sql += "and um.is_read = ?";
            page = dbTool.findAll(MsgVo.class, pageable, sql, userId, read);
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
        Long scalar = (Long) dbTool.findScalar(sql, userId, false);


        return scalar == null ? 0 : scalar;
    }

    public void read(String id) {
        SysMsgUser db = msgUserDao.findOne(id);
        db.setReadTime(LocalDateTime.now());
        db.setIsRead(true);
        msgUserDao.save(db);
    }
}
