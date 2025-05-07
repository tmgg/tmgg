package io.tmgg.framework.session;

import io.tmgg.framework.cache.CacheService;
import io.tmgg.web.perm.Subject;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参考了shiro 的权限管理
 */
@Slf4j
@Component
public class SysHttpSessionService {


    public static final String CACHE_NAME = "sysHttpSession";

    private final Cache<String, SysHttpSession> cache;


    public SysHttpSessionService(CacheService cacheService) {
        Duration timeout = Duration.ofDays(1); // 缓存这里放宽泛点, 由MySessionRepository精确控制
        cache = cacheService.newCache(CACHE_NAME, SysHttpSession.class, 1000, 10, timeout);
    }


    public List<Subject> findAllSubject() {
        List<SysHttpSession> list = findAll();

        List<Subject> subjectList = list.stream()
                .filter(session -> null != session.getAttribute(SysHttpSession.SUBJECT_KEY))
                .map(session -> (Subject) session.getAttribute(SysHttpSession.SUBJECT_KEY)).collect(Collectors.toList());

        return subjectList;
    }


    public void forceExistBySessionId(String sessionId) {
        cache.remove(sessionId);
    }

    public void forceExistBySubjectId(String subjectId) {
        List<SysHttpSession> sessionList = findAll();

        for (SysHttpSession session : sessionList) {
            Subject subject = session.getAttribute(SysHttpSession.SUBJECT_KEY);
            boolean ok = subject != null && subject.getId().equals(subjectId);
            if (ok) {
                cache.remove(session.getId());
            }
        }

    }

    public List<SysHttpSession> findAll() {
        List<SysHttpSession> sessionList = new ArrayList<>();
        try {
            Iterator<Cache.Entry<String, SysHttpSession>> iter = cache.iterator();
            while (iter.hasNext()) {
                Cache.Entry<String, SysHttpSession> e = iter.next();
                SysHttpSession session = e.getValue();
                sessionList.add(session);

            }
        } catch (Exception ex) {
            log.error("获取在线用户列表时失败，可能是多个系统共用一个缓存文件夹照成，可尝试删除", ex);
        }

        return sessionList;
    }

    public void put(String id, SysHttpSession session) {
        cache.put(id, session);
    }

    public SysHttpSession get(String id) {
        SysHttpSession session = cache.get(id);
        return session;
    }

    public void remove(String id) {
        cache.remove(id);
    }
}
