package io.tmgg.framework.session.config;

import cn.hutool.core.util.IdUtil;
import io.tmgg.framework.session.SysHttpSession;
import io.tmgg.framework.session.SysHttpSessionDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.session.SessionRepository;

import java.time.Duration;
import java.time.Instant;

/**
 * 参考 MapSessionRepository
 */
@Slf4j
@CacheConfig(cacheNames = "sysSession")
public class MySessionRepository implements SessionRepository<SysHttpSession> {

    @Resource
    private SysHttpSessionDao dao;



    @Override
    public SysHttpSession createSession() {
        SysHttpSession session = new SysHttpSession(IdUtil.simpleUUID());
        session.setCreationTime(Instant.now());
        session.setMaxInactiveInterval(Duration.ofHours(1));

        log.info("创建session {}", session.getId());
        return session;
    }


    @CacheEvict(key = "#session.id")
    @Override
    public void save(SysHttpSession session) {
        log.info("保存session {}", session.getId());
        System.out.println(session);
        dao.save(session);

    }

    @Override
    @Cacheable(key = "#id")
    public SysHttpSession findById(String id) {
        log.info("查询session {}", id);
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
    @CacheEvict(key = "#id")
    public void deleteById(String id) {
        log.info("删除session {}",id);
        dao.deleteById(id);
        dao.cleanExpired();
    }


}
