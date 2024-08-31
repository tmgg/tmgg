
package io.tmgg.sys.role.service;

import io.tmgg.lang.CodeException;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.perm.SysPermDao;
import io.tmgg.sys.perm.SysPerm;
import io.tmgg.sys.role.dao.SysRoleDao;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.enums.SysRoleExceptionEnum;
import io.tmgg.sys.role.param.SysRoleParam;
import io.tmgg.sys.dao.SysUserDao;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统角色service接口实现类
 */
@Service
public class SysRoleService extends BaseService<SysRole> {


    @Resource
    private SysRoleDao roleDao;

    @Resource
    private SysPermDao menuDao;

    @Resource
    private SysUserDao sysUserDao;


    /**
     * 角色授权
     */
    @Transactional
    public void grantPerm(String roleId, Collection<String> ids) {
        SysRole role = roleDao.findOne(roleId);

        Assert.state(!role.getBuiltin(), "内置角色不能修改");
        List<SysPerm> newMenus = menuDao.findAllById(ids);

        List<String> perms = newMenus.stream().map(SysPerm::getPerm).filter(Objects::nonNull).toList();
        role.setPerms(perms);
        roleDao.save(role);
    }


    public SysRole getByCode(String code) {
        JpaQuery<SysRole> query = new JpaQuery<>();
        query.eq(SysRole.Fields.code, code);

        return this.findOne(query);
    }


    @Transactional
    public Set<SysRole> getLoginRoles(String userId) {
        Assert.state(userId != null, "用户ID不能为空");
        SysUser user = sysUserDao.findOne(userId);

        Collection<SysRole> roles = user.getRoles();


        return roles.stream().filter(r -> r.getStatus() == CommonStatus.ENABLE).collect(Collectors.toSet());
    }


    public List<SysRole> list() {
        JpaQuery<SysRole> queryWrapper = new JpaQuery<>();
        //只查询正常状态
        queryWrapper.eq(SysRole.Fields.status, CommonStatus.ENABLE);
        return this.findAll(queryWrapper);
    }


    @Override
    public SysRole saveOrUpdate(SysRole input) throws Exception {
        SysRole sysRole = super.saveOrUpdate(input);
        Assert.state(!sysRole.getBuiltin(), "内置角色不能修改");
        return sysRole;
    }

    @Override
    public void deleteById(String id) {
        Assert.hasText(id, "id不能为空");

        SysRole db = baseDao.findOne(id);
        Assert.state(!db.getBuiltin(), "内置角色不能删除");
        baseDao.deleteById(id);
    }

    public List<SysRole> findValid() {
        JpaQuery<SysRole> q = new JpaQuery<>();
        q.eq(SysRole.Fields.status, CommonStatus.ENABLE);
        return this.findAll(q);
    }

    // TODO
    public List<String> ownMenu(String roleId) {
        SysRole role = this.findOne(roleId);
        return role.getPerms();
    }


    public List<SysRole> findAllByCode(Set<String> roles) {
        JpaQuery<SysRole> q = new JpaQuery<>();
        q.in(SysRole.Fields.code, roles);
        return this.findAll(q);
    }

    @Transactional
    public void initDefaultUserRole() {
        long count = roleDao.countByCode(SysRole.DEFAULT_ROLE);
        if (count > 0) {
            return;
        }
        SysRole sysRole = new SysRole();
        sysRole.setId(SysRole.DEFAULT_ROLE);
        sysRole.setCode(SysRole.DEFAULT_ROLE);
        sysRole.setName("默认角色");
        sysRole.setStatus(CommonStatus.ENABLE);
        sysRole.setRemark("创建用户时的默认角色");
        sysRole.setBuiltin(true);

        roleDao.save(sysRole);
    }

    @Transactional
    public SysRole initDefaultAdmin() {
        String roleCode = "admin";
        SysRole role = roleDao.findByCode(roleCode);
        if (role != null ) {
            return role;
        }
        SysRole sysRole = new SysRole();
        sysRole.setId(roleCode);
        sysRole.setCode(roleCode);
        sysRole.setName("管理员");
        sysRole.setStatus(CommonStatus.ENABLE);
        sysRole.setPerms(List.of("*"));
        sysRole.setBuiltin(true);
        sysRole.setRemark("内置管理员");

        return roleDao.save(sysRole);
    }
}
