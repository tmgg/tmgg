package io.tmgg.framework.session.config;

import cn.hutool.core.util.IdUtil;
import io.tmgg.framework.dbconfig.DbValue;
import io.tmgg.framework.session.SysHttpSession;
import io.tmgg.framework.session.SysHttpSessionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.SessionRepository;

import java.time.Duration;
import java.time.Instant;

/**
 * 参考 MapSessionRepository
 */
@Slf4j
public class MySessionRepository implements SessionRepository<SysHttpSession> {

    @DbValue("sys.sessionIdleTime")
    private int timeToIdleExpiration;

    @Resource
    private SysHttpSessionService service;




    @Override
    public SysHttpSession createSession() {
        SysHttpSession session = new SysHttpSession(IdUtil.simpleUUID());
        session.setCreationTime(Instant.now());
        session.setMaxInactiveInterval(Duration.ofMinutes(timeToIdleExpiration));
        log.info("创建session {},过期时间 {}分钟", session.getId(), timeToIdleExpiration);
        return session;
    }


    @Override
    public void save(SysHttpSession session) {
        service.put(session.getId(), session);
    }

    @Override
    public SysHttpSession findById(String id) {
        try {
            SysHttpSession session = service.get(id);
            if (session == null) {
                return null;
            }

            if (session.isExpired()) {
                return null;
            }

            if (session.getSessionAttrs() == null) {
                return null;
            }

            return session;
        } catch (Exception e) {
            log.error("获取缓存cache失败 {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        service.remove(id);
    }


}
