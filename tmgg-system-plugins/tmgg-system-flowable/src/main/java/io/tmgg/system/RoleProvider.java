package io.tmgg.system;


import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.assignment.Identity;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.service.SysRoleService;
import io.tmgg.modules.sys.service.SysUserService;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleProvider implements AssignmentTypeProvider {

    @Resource
    SysRoleService roleService;

    @Resource
    SysUserService userService;

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public String getCode() {
        return "role";
    }

    @Override
    public String getLabel() {
        return "候选角色";
    }

    @Override
    public Collection<Identity> findAll() {
        List<SysRole> list = roleService.findAll();

        List<Identity> result = new ArrayList<>();
        for (SysRole p : list) {
            Identity identity = new Identity(String.valueOf(p.getId()), null, p.getName(), false, true);
            result.add(identity);
        }
        return result;
    }

    @Override
    public String getLabelById(String id) {
        SysRole role = roleService.findOne(id);
        return role.getName();
    }

    @Override
    public XmlAttribute getXmlAttribute() {
        //return "candidateGroups";
        return XmlAttribute.candidateGroups;
    }


    @Override
    public List<String> findGroupsByUser(String currentLoginUserId) {
        SysUser one = userService.findOne(currentLoginUserId);

        return one.getRoles().stream().map(BaseEntity::getId).collect(Collectors.toList());
    }


}
