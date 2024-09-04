package io.tmgg.web.session.db;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import io.tmgg.web.session.db.SysHttpSession;
import io.tmgg.web.session.db.SysHttpSessionDao;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySessionRepository implements SessionRepository<MapSession> {

    @Resource
    private SysHttpSessionDao sysHttpSessionDao;


    @Override
    public MapSession createSession() {
        return new MapSession("spring-session-"+IdUtil.simpleUUID());
    }

    @Override
    public void save(MapSession session) {
        if (session.isExpired()) {
            sysHttpSessionDao.deleteById(session.getId());
        } else {
            SysHttpSession sysHttpSession = sysHttpSessionDao.findBySessionId(session.getId());
            if(sysHttpSession == null){
                sysHttpSession = new SysHttpSession();
                sysHttpSession.setSessionId(session.getId());
                sysHttpSession.setInvalidated(false);
            }
            LocalDateTime localDateTime = DateUtil.toLocalDateTime(session.getLastAccessedTime());
            sysHttpSession.setLastAccessedTime(DateUtil.date(localDateTime));
            sysHttpSession.setSession(session);
            sysHttpSession.setExpired(session.isExpired());
            sysHttpSessionDao.save(sysHttpSession);
        }
    }

    @Override
    public MapSession findById(String id) {
        SysHttpSession session = sysHttpSessionDao.findBySessionId(id);
        if(session ==null){
            return null;
        }
        return session.getSession();
    }

    @Override
    public void deleteById(String id) {
        sysHttpSessionDao.deleteBySessionId(id);
    }


}
