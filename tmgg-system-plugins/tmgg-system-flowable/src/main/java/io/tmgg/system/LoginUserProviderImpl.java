package io.tmgg.system;

import io.tmgg.flowable.FlowableLoginUser;
import io.tmgg.flowable.FlowableLoginUserProvider;
import io.tmgg.modules.system.entity.SysOrg;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.modules.system.service.SysOrgService;
import io.tmgg.modules.system.service.SysUserService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class LoginUserProviderImpl implements FlowableLoginUserProvider {

    @Resource
    SysOrgService sysOrgService;


    @Override
    public FlowableLoginUser currentLoginUser() {
        Subject subject = SecurityUtils.getSubject();

        FlowableLoginUser fu = new FlowableLoginUser();
        fu.setId(subject.getId());
        fu.setName(subject.getName());
        fu.setSuperAdmin(subject.hasPermission("*"));
        fu.setDeptId(subject.getDeptId());
        fu.setDeptName(subject.getDeptName());
        fu.setUnitName(subject.getUnitId());
        fu.setUnitName(subject.getUnitName());


        // 获取部门领导
        if(fu.getDeptId() != null){
            SysOrg org = sysOrgService.findOne(fu.getDeptId());

            if(org != null){
                SysUser deptLeader = org.getLeader();
                if(deptLeader != null){
                    fu.setDeptLeaderId(deptLeader.getId());
                }
            }
        }

        return fu;
    }

}
