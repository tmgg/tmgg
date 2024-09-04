package io.tmgg.web.perm;

import io.tmgg.web.session.db.SysHttpSession;
import io.tmgg.web.session.db.SysHttpSessionDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参考了shiro 的权限管理
 */
@Slf4j
@Component
public class SecurityManager {

    public static final String SESSION_KEY = "SUBJECT";

    @Resource
    SysHttpSessionDao sm;


    public List<Subject> findAll() {
        List<SysHttpSession> list = sm.findAll();
        List<Subject> subjectList = list.stream()
                .filter(session -> null != session.getAttribute(SESSION_KEY))
                .map(session -> (Subject) session.getAttribute(SESSION_KEY)).collect(Collectors.toList());

        return subjectList;
    }


    public Subject findBySessionId(String sessionId) {
        SysHttpSession httpSession = sm.findOne(sessionId);
        if (httpSession != null) {
            return httpSession.getAttribute(SESSION_KEY);
        }
        return null;
    }

    public void forceExistBySessionId(String sessionId) {
        sm.invalidate(sessionId);


    //    HttpSession httpSession = sm.find(sessionId);
      //  httpSession.invalidate();
    }

    public void forceExistBySubjectId(String subjectId) {
        List<SysHttpSession> sessionList = sm.findAll();

        List<String> sessionIds = sessionList.stream()
                .filter(session -> {
                    Subject subject = session.getAttribute(SESSION_KEY);
                    return subject != null && subject.getId().equals(subjectId);
                }).map(Session::getId)
                .toList();


        for (String sessionId : sessionIds) {
            this.forceExistBySessionId(sessionId);
        }


//        for (HttpSession httpSession : sessionList) {
//            httpSession.invalidate();
//        }

    }

}
