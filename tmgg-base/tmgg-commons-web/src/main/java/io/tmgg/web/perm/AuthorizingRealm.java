package io.tmgg.web.perm;


import jakarta.servlet.http.HttpSession;

import java.util.Comparator;

public interface AuthorizingRealm extends Comparator<AuthorizingRealm> {

    Subject doGetSubject(HttpSession session, String userId);

    void doGetPermissionInfo(Subject subject);


    default String prefix() {
        return "/";
    }


    @Override
    default int compare(AuthorizingRealm o1, AuthorizingRealm o2) {
        // 长度倒叙
        return  -(o1.prefix().length() - o2.prefix().length());
    }
}
