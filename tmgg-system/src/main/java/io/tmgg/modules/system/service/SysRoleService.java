
package io.tmgg.modules.system.service;

import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.modules.system.dao.SysMenuDao;
import io.tmgg.modules.system.dao.SysRoleDao;
import io.tmgg.modules.system.dao.SysUserDao;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.data.query.JpaQuery;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统角色service接口实现类
 */
@Slf4j
@Service
public class SysRoleService extends BaseService<SysRole> {


    @Resource
    private SysRoleDao roleDao;

    @Resource
    private SysMenuDao sysMenuDao;

    @Resource
    private SysUserDao sysUserDao;


    @Resource
    private SysHttpSessionService sm;


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

    @Transactional
    public List<SysMenu> ownMenu(String id) {
        SysRole role = roleDao.findOne(id);
        List<SysMenu> menuList;

        if (role.getCode().equals("admin")) {
            menuList = sysMenuDao.findAll();
        } else {
            menuList = role.getMenus();
        }

        // 去重排序
        return menuList.stream().distinct().sorted(Comparator.comparing(SysMenu::getSeq)).toList();
    }

    @Transactional
    public List<SysMenu> ownMenu(Iterable<SysRole> roles) {
        List<SysMenu> menuList = new LinkedList<>();

        for (SysRole role : roles) {
            List<SysMenu> menus = this.ownMenu(role.getId());
            menuList.addAll(menus);
        }


        return menuList.stream().distinct().sorted(Comparator.comparing(SysMenu::getSeq)).toList();
    }


    public List<SysUser> findUsers(String roleId) {
        List<SysUser> userList = sysUserDao.findByRoleId(roleId);

        return userList;
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
        if (role != null) {
            return role;
        }
        SysRole sysRole = new SysRole();
        sysRole.setCode(roleCode);
        sysRole.setName("管理员");
        sysRole.setPerms(List.of("*"));
        sysRole.setBuiltin(true);
        sysRole.setRemark("系统生成");

        return roleDao.save(sysRole);
    }

    @Transactional
    public void grantUsers(String id, List<String> userIdList) {
        SysRole role = roleDao.findOne(id);
        role.getUsers().clear();

        List<SysUser> users = sysUserDao.findAllById(userIdList);
        role.getUsers().addAll(users);
    }


    @Transactional
    public void grantMenu(String id, List<String> menuIds) {
        SysRole role = roleDao.findOne(id);
        List<SysMenu> menus = sysMenuDao.findAllById(menuIds);
        role.setMenus(menus);
        roleDao.save(role);

        // 刷新 登录用户的权限
        List<Subject> list = sm.findAllSubject();
        for (Subject subject : list) {
            if (subject.hasRole(role.getCode())) {
                sm.forceExistBySubjectId(subject.getId());
                log.info("强制退出用户 {} [{}]", subject.getName(), subject.getAccount());
            }
        }
    }
}
