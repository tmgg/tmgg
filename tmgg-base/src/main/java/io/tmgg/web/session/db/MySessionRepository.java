package io.tmgg.web.session.db;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 参考 MapSessionRepository
 */
public class MySessionRepository implements SessionRepository<MapSession> {

    @Resource
    private SysHttpSessionDao dao;


    @Override
    public MapSession createSession() {
        MapSession mapSession = new MapSession("session-" + IdUtil.simpleUUID());
        mapSession.setMaxInactiveInterval(Duration.ofSeconds(29));
        return mapSession;
    }

    @Override
    public void save(MapSession session) {
        if (!session.getId().equals(session.getOriginalId())) {
            dao.invalidate(session.getOriginalId());
        }

        SysHttpSession sysSession = dao.findBySessionId(session.getId());
        if (sysSession == null && session.isExpired()) {
            // 数据库已删除，已过期，就不再保存
            return;
        }

        if (sysSession == null) {
            sysSession = new SysHttpSession();
            sysSession.setSessionId(session.getId());
            sysSession.setInvalidated(false);
        }
        LocalDateTime localDateTime = DateUtil.toLocalDateTime(session.getLastAccessedTime());
        sysSession.setLastAccessedTime(DateUtil.date(localDateTime));
        sysSession.setSession(session);
        sysSession.setExpired(session.isExpired());
        sysSession.setMaxInactiveInterval(session.getMaxInactiveInterval());
        dao.save(sysSession);
    }

    @Override
    public MapSession findById(String id) {
        SysHttpSession session = dao.findBySessionId(id);
        if (session == null) {
            return null;
        }

        if (session.isInvalidated()) {
            return null;
        }
        if(session.isExpired()){
            return null;
        }

        return session.getSession();
    }

    @Override
    public void deleteById(String id) {
        dao.invalidate(id);
    }


}
