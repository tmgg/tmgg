package io.tmgg.modules.system;

import io.tmgg.flowable.FlowableLoginUser;
import io.tmgg.flowable.FlowableLoginUserProvider;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import org.springframework.stereotype.Component;


@Component
public class LoginUserProviderImpl implements FlowableLoginUserProvider {

    @Override
    public FlowableLoginUser currentLoginUser() {
        Subject current = SecurityUtils.getSubject();

        FlowableLoginUser user = new FlowableLoginUser();
        user.setId(current.getId());
        user.setName(current.getName());
        user.setSuperAdmin(current.hasPermission("*"));

        return user;
    }

}
