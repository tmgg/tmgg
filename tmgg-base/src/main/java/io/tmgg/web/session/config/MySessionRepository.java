package io.tmgg.web.session.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import io.tmgg.web.session.db.SysHttpSession;
import io.tmgg.web.session.db.SysHttpSessionDao;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 参考 MapSessionRepository
 */
public class MySessionRepository implements SessionRepository<SysHttpSession> {

    @Resource
    private SysHttpSessionDao dao;


    @Override
    public SysHttpSession createSession() {
        SysHttpSession session = new SysHttpSession(IdUtil.simpleUUID());
        session.setCreationTime(Instant.now());
        session.setMaxInactiveInterval(Duration.ofHours(1));
        return session;
    }

    @Override
    public void save(SysHttpSession session) {
        dao.save(session);
    }

    @Override
    public SysHttpSession findById(String id) {
        SysHttpSession session = dao.findOne(id);
        if (session == null) {
            return null;
        }

        if (session.isInvalidated()) {
            return null;
        }
        if(session.isExpired()){
            return null;
        }

        return session;
    }

    @Override
    public void deleteById(String id) {
        dao.invalidate(id);
    }


}
