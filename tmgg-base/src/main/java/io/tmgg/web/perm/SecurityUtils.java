package io.tmgg.web.perm;

import io.tmgg.lang.RequestTool;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import io.tmgg.web.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 参考了shiro 的权限管理
 */
@Slf4j
public class SecurityUtils {


    // uuid
    private static final Cache<String, Subject> TOKEN_SUBJECT_CACHE = CacheUtil.newTimedCache(1000 * 60 * 60 * 2);


    // 非法token， value无意义
    private static final Cache<String, Object> LOGOUT_TOKEN = CacheUtil.newTimedCache(1000L * 60 * 60 * 24 * 30);

    public static final String SUBJECT_REQUEST_KEY = "SUBJECT";


    /**
     * 获取当前线程的用户
     *
     *
     */
    public static Subject getSubject() {
        HttpServletRequest request = RequestTool.currentRequest();

        Subject subject = null;
        if (request != null) {
            subject = (Subject) request.getAttribute(SUBJECT_REQUEST_KEY);
        }

        if (subject == null) {
            subject = new Subject();
        }

        return subject;
    }

    /**
     * 仅权限模块调用，业务代码中请勿调用
     *
     * @param token
     *
     */
    public static Subject getCachedSubjectByToken(String token) {
        return TOKEN_SUBJECT_CACHE.get(token);
    }

    public static void login(String token, Subject subject) {
        if (LOGOUT_TOKEN.containsKey(token)) {
            throw new SystemException(401, "登录凭证已注销，请重新登录");
        }

        TOKEN_SUBJECT_CACHE.put(token, subject);
        subject.login(token);

        HttpServletRequest request = RequestTool.currentRequest();
        request.setAttribute(SUBJECT_REQUEST_KEY, subject);
    }


    public static void logout(String token) {
        Assert.hasText(token, "token不能为空");
        LOGOUT_TOKEN.put(token, StringUtils.EMPTY);
        TOKEN_SUBJECT_CACHE.remove(token);
    }


    public static List<Subject> findAll(String subjectId) {
        List<Subject> list = new ArrayList<>(3);
        Iterator<CacheObj<String, Subject>> ite = TOKEN_SUBJECT_CACHE.cacheObjIterator();
        while (ite.hasNext()) {
            CacheObj<String, Subject> next = ite.next();
            Subject subject = next.getValue();

            if (subjectId.equals(subject.getId())) {
                list.add(subject);
            }
        }
        return list;
    }

    public static List<Subject> findAll() {
        List<Subject> list = new ArrayList<>(100);
        Iterator<CacheObj<String, Subject>> ite = TOKEN_SUBJECT_CACHE.cacheObjIterator();
        while (ite.hasNext()) {
            CacheObj<String, Subject> next = ite.next();
            Subject subject = next.getValue();
            list.add(subject);
        }
        return list;
    }


    // 移除后再次请求就会刷新
    public static void refresh(String subjectId) {
        List<Subject> list = findAll(subjectId);

        for (Subject subject : list) {
            if (subject.getId().equals(subjectId)) {
                TOKEN_SUBJECT_CACHE.remove(subject.getToken());
            }
        }
    }
}
