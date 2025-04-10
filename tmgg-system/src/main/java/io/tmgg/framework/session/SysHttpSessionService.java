package io.tmgg.framework.session;

import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参考了shiro 的权限管理
 */
@Slf4j
@Component
public class SysHttpSessionService {





    @Resource
    private Cache<String, SysHttpSession> httpSessionCache;

    public  List<Subject> findAllSubject() {
        List<SysHttpSession> list = findAllSession();

        List<Subject> subjectList = list.stream()
                .filter(session -> null != session.getAttribute(SysHttpSession.SUBJECT_KEY))
                .map(session -> (Subject) session.getAttribute(SysHttpSession.SUBJECT_KEY)).collect(Collectors.toList());

        return subjectList;
    }


    public  void forceExistBySessionId(String sessionId) {
            httpSessionCache.remove(sessionId);
    }

    public  void forceExistBySubjectId(String subjectId) {
        List<SysHttpSession> sessionList = findAllSession();

        for (SysHttpSession session : sessionList) {
            Subject subject = session.getAttribute(SysHttpSession.SUBJECT_KEY);
            boolean ok = subject != null && subject.getId().equals(subjectId);
            if(ok){
                httpSessionCache.remove(session.getId());
            }
        }

    }

    public  List<SysHttpSession> findAllSession() {
        List<SysHttpSession> sessionList = new ArrayList<>();
        try {
        for (Cache.Entry<String, SysHttpSession> e : httpSessionCache) {

                SysHttpSession session = e.getValue();
                sessionList.add(session);


        }   } catch (Exception ex) {
            log.error("获取在线用户列表时失败，可能是多个系统共用一个缓存文件夹照成，可尝试删除", ex);
        }
        return sessionList;
    }

}
