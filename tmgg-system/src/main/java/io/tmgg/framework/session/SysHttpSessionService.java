package io.tmgg.framework.session;

import io.tmgg.web.perm.Subject;
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
public class SysHttpSessionService {

    public static final String SESSION_KEY = "SUBJECT";


    @Resource
    SysHttpSessionDao dao;

    public List<Subject> findAll() {
        List<SysHttpSession> list = dao.findAll();
        List<Subject> subjectList = list.stream()
                .filter(session -> null != session.getAttribute(SESSION_KEY))
                .map(session -> (Subject) session.getAttribute(SESSION_KEY)).collect(Collectors.toList());

        return subjectList;
    }


    public void forceExistBySessionId(String sessionId) {
        dao.deleteById(sessionId);
    }

    public void forceExistBySubjectId(String subjectId) {
        List<SysHttpSession> sessionList = dao.findAll();

        List<String> sessionIds = sessionList.stream()
                .filter(session -> {
                    Subject subject = session.getAttribute(SESSION_KEY);
                    return subject != null && subject.getId().equals(subjectId);
                }).map(Session::getId)
                .toList();


        for (String sessionId : sessionIds) {
            dao.deleteById(sessionId);
        }

    }

}
