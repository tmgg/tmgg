package io.tmgg.web.session.config;

import cn.hutool.core.util.IdUtil;
import io.tmgg.web.session.db.SysHttpSession;
import io.tmgg.web.session.db.SysHttpSessionDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.CacheManager;
import org.ehcache.core.EhcacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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

    @Autowired
    private EhcacheManager cacheManager;

    @PostConstruct
    public void init(){

        System.out.println(cacheManager);
    }

    @Override
    public SysHttpSession createSession() {
        SysHttpSession session = new SysHttpSession(IdUtil.simpleUUID());
        session.setCreationTime(Instant.now());
        session.setMaxInactiveInterval(Duration.ofHours(1));

        log.info("创建session {}", session.getId());
        return session;
    }

    @Override
    public void save(SysHttpSession session) {
        log.info("保存session {}", session.getId());
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
