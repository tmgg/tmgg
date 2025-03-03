
package io.tmgg.modules.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.dao.SysMenuDao;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.dao.SysRoleDao;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.dao.SysUserDao;
import io.tmgg.modules.sys.entity.SysUser;
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
    private SysMenuDao sysMenuDao;

    @Resource
    private SysUserDao sysUserDao;


    /**
     * 角色授权
     */
    @Transactional
    public void grantPerm(String roleId, Collection<String> ids) {
        SysRole role = roleDao.findOne(roleId);

        Assert.state(!role.getBuiltin(), "内置角色不能修改");
        List<SysMenu> newMenus = sysMenuDao.findAllById(ids);

        List<String> perms = newMenus.stream().map(SysMenu::getPerm).filter(Objects::nonNull).toList();
        role.setPerms(perms);
        roleDao.save(role);
    }


    public SysRole findByCode(String code) {
        JpaQuery<SysRole> query = new JpaQuery<>();
        query.eq(SysRole.Fields.code, code);

        return this.findOne(query);
    }


    @Transactional
    public Set<SysRole> getLoginRoles(String userId) {
        Assert.state(userId != null, "用户ID不能为空");
        SysUser user = sysUserDao.findOne(userId);

        Collection<SysRole> roles = user.getRoles();


        return roles.stream().filter(SysRole::getEnabled).collect(Collectors.toSet());
    }


    @Override
    public SysRole saveOrUpdate(SysRole input) throws Exception {
        boolean isNew = input.isNew();
        if (isNew) {
            return baseDao.save(input);
        }

        SysRole old = baseDao.findOne(input);
        Assert.state(!old.getBuiltin(), "内置角色不能修改");

        BeanUtil.copyProperties(input, old,
                CopyOptions.create().setIgnoreProperties(
                ArrayUtil.append( BaseEntity.BASE_ENTITY_FIELDS, SysRole.Fields.perms)
               ));

        return   baseDao.save(old);
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
        q.eq(SysRole.Fields.enabled, true);
        return this.findAll(q);
    }

    public List<String> ownMenu(String roleId) {
        SysRole role = this.findOne(roleId);
        List<String> perms = role.getPerms();

        List<SysMenu> list  = sysMenuDao.findByPerms(perms);

        return list.stream().map(SysMenu::getId).collect(Collectors.toList());
    }


    public List<SysRole> findAllByCode(Set<String> roles) {
        JpaQuery<SysRole> q = new JpaQuery<>();
        q.in(SysRole.Fields.code, roles);
        return this.findAll(q);
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
        sysRole.setPerms(List.of("*"));
        sysRole.setBuiltin(true);
        sysRole.setRemark("内置管理员");

        return roleDao.save(sysRole);
    }
}
