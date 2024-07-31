package io.tmgg.web.perm;


import java.util.Comparator;

public interface AuthorizingRealm extends Comparator<AuthorizingRealm> {

    Subject doGetSubject(String token);

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
