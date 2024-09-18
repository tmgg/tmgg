package io.tmgg.web.session.config;

import cn.hutool.core.util.IdUtil;
import io.tmgg.web.session.db.SysHttpSession;
import io.tmgg.web.session.db.SysHttpSessionDao;
import jakarta.annotation.Resource;
import org.springframework.session.SessionRepository;

import java.time.Duration;
import java.time.Instant;

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


        if(session.isExpired()){
            return null;
        }

        if(session.getSessionAttrs() == null){
            dao.deleteById(session.getId());
            return null;
        }

        return session;
    }

    @Override
    public void deleteById(String id) {
        dao.deleteById(id);

        dao.cleanExpired();
    }


}
