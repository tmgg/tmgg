package io.tmgg.framework.session.config;

import cn.hutool.core.util.IdUtil;
import io.tmgg.framework.session.SysHttpSession;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.session.SessionRepository;

import java.time.Duration;
import java.time.Instant;

/**
 * 参考 MapSessionRepository
 */
@Slf4j
public class MySessionRepository implements SessionRepository<SysHttpSession> {


    @Resource
    private Cache<String, SysHttpSession> httpSessionCache;



    @Override
    public SysHttpSession createSession() {
        SysHttpSession session = new SysHttpSession(IdUtil.simpleUUID());
        session.setCreationTime(Instant.now());
        session.setMaxInactiveInterval(Duration.ofHours(1));

        log.trace("创建session {}", session.getId());
        return session;
    }


    @Override
    public void save(SysHttpSession session) {
        log.trace("保存session {}", session.getId());
        httpSessionCache.put(session.getId(), session);
    }

    @Override
    public SysHttpSession findById(String id) {
        log.trace("查询session {}", id);
        SysHttpSession session = httpSessionCache.get(id);
        if (session == null) {
            return null;
        }

        if(session.isExpired()){
            return null;
        }

        if(session.getSessionAttrs() == null){
            return null;
        }

        return session;
    }

    @Override
    public void deleteById(String id) {
        log.debug("删除session {}",id);
        httpSessionCache.remove(id);
    }


}
