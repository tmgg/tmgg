package io.tmgg.web.session;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MySessionRepository implements SessionRepository<MapSession> {

    @Resource
    private SysHttpSessionDao sysHttpSessionDao;

    private final ConcurrentHashMap<String, MapSession> sessionStore = new ConcurrentHashMap<>();


    @PostConstruct
    private void init() {
        List<SysHttpSession> list = sysHttpSessionDao.findAll();

        for (SysHttpSession session : list) {
            MapSession mapSession = SerializationUtils.deserialize(session.getPayload());
            if (mapSession.isExpired()) {
                sysHttpSessionDao.delete(session);
            } else {
                sessionStore.put(session.getId(), mapSession);
            }

        }
    }

    @Override
    public MapSession createSession() {
        init();
        MapSession session = new MapSession(IdUtil.simpleUUID());
        return session;
    }

    @Override
    public void save(MapSession session) {
        if (session.isExpired()) {
            sessionStore.remove(session.getId());
            sysHttpSessionDao.deleteById(session.getId());
        } else {
            sessionStore.put(session.getId(), session);

            byte[] bytes = SerializationUtils.serialize(session);
            SysHttpSession sysHttpSession = new SysHttpSession();
            sysHttpSession.setId(session.getId());
            sysHttpSession.setPayload(bytes);
            sysHttpSessionDao.save(sysHttpSession);
        }
    }

    @Override
    public MapSession findById(String id) {
        return sessionStore.get(id);
    }

    @Override
    public void deleteById(String id) {
        sessionStore.remove(id);
        sysHttpSessionDao.deleteById(id);
    }


    public List<Session> findByAttr(String key, String value) {
        List<Session> sessions = new ArrayList<>();
        for (MapSession session : sessionStore.values()) {
            Object attribute = session.getAttribute(key);
            if (attribute != null && attribute.equals(value)) {
                sessions.add(session);
            }
        }
        return sessions;
    }

    private void deleteByAttr(String key, String value) {
        List<Session> arr = findByAttr(key, value);
        for (Session session : arr) {
            this.deleteById(session.getId());
        }

    }

    public void deleteBySubject(String subjectId) {
        deleteByAttr("subjectId", subjectId);


    }

}
