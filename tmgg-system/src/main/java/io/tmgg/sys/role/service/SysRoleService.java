
package io.tmgg.sys.role.service;

import io.tmgg.lang.CodeException;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.menu.dao.SysMenuDao;
import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.sys.role.dao.SysRoleDao;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.enums.SysRoleExceptionEnum;
import io.tmgg.sys.role.param.SysRoleParam;
import io.tmgg.sys.user.dao.SysUserDao;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.web.enums.AdminType;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
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
    private SysMenuDao menuDao;

    @Resource
    private SysUserDao sysUserDao;


    /**
     * 角色授权
     *
     * @param roleId
     * @param grantMenuIdList
     */
    @Transactional
    public void grantMenu(String roleId, Collection<String> grantMenuIdList) {
        SysRole role = roleDao.findOne(roleId);
        List<SysMenu> newMenus = menuDao.findAllById(grantMenuIdList);


        Set<SysMenu> menus = role.getMenus();
        menus.clear();
        menus.addAll(newMenus);
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

        Collection<SysRole> roles;
        if (user.getAdminType() == AdminType.SUPER_ADMIN) {
            roles = roleDao.findAll();
        } else {
            roles = user.getRoles();
        }


        return roles.stream().filter(r -> r.getStatus() == CommonStatus.ENABLE).collect(Collectors.toSet());
    }


    public List<SysRole> list() {
        JpaQuery<SysRole> queryWrapper = new JpaQuery<>();
        //只查询正常状态
        queryWrapper.eq(SysRole.Fields.status, CommonStatus.ENABLE);
        return this.findAll(queryWrapper);
    }


    public void add(SysRoleParam sysRoleParam) {
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, false);
        SysRole sysRole = new SysRole();
        BeanUtil.copyProperties(sysRoleParam, sysRole);
        sysRole.setStatus(CommonStatus.ENABLE);
        this.save(sysRole);
    }

    public void delete(SysRoleParam sysRoleParam) {
        roleDao.deleteById(sysRoleParam.getId());
    }


    public void edit(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.findOne(sysRoleParam.getId());
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, true);
        BeanUtil.copyProperties(sysRoleParam, sysRole, SysRole.Fields.status);
        this.save(sysRole);
    }


    public SysRole detail(SysRoleParam sysRoleParam) {
        return this.findOne(sysRoleParam.getId());
    }


    public String getNameByRoleId(String roleId) {
        SysRole sysRole = this.findOne(roleId);
        if (ObjectUtil.isEmpty(sysRole)) {
            throw new CodeException(SysRoleExceptionEnum.ROLE_NOT_EXIST);
        }
        return sysRole.getName();
    }


    /**
     * 校验参数，检查是否存在相同的名称和编码
     *
 *
     */
    private void checkParam(SysRoleParam sysRoleParam, boolean isExcludeSelf) {
        String id = sysRoleParam.getId();
        String name = sysRoleParam.getName();
        String code = sysRoleParam.getCode();

        JpaQuery<SysRole> queryWrapperByName = new JpaQuery<>();
        queryWrapperByName.eq(SysRole.Fields.name, name)
                .ne(SysRole.Fields.status, CommonStatus.DELETED);

        JpaQuery<SysRole> queryWrapperByCode = new JpaQuery<>();
        queryWrapperByCode.eq(SysRole.Fields.code, code)
                .ne(SysRole.Fields.status, CommonStatus.DELETED);

        //是否排除自己，如果排除自己则不查询自己的id
        if (isExcludeSelf) {
            queryWrapperByName.ne("id", id);
            queryWrapperByCode.ne("id", id);
        }
        long countByName = this.count(queryWrapperByName);
        long countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new CodeException(SysRoleExceptionEnum.ROLE_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new CodeException(SysRoleExceptionEnum.ROLE_CODE_REPEAT);
        }
    }


    public List<SysRole> findValid() {
        JpaQuery<SysRole> q = new JpaQuery<>();
        q.eq(SysRole.Fields.status, CommonStatus.ENABLE);
        return this.findAll(q);
    }

    public Set<String> ownMenu(String roleId) {
        SysRole role = this.findOne(roleId);

        return role.getMenus().stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    @Transactional
    public void changeRoleUser(String roleId, List<SysUser> add, List<SysUser> remove) {
        SysRole one = roleDao.findOne(roleId);

        one.getUsers().addAll(add);
        one.getUsers().removeAll(remove);
    }

    public List<SysRole> findAllByCode(Set<String> roles) {
        JpaQuery<SysRole> q = new JpaQuery<>();
        q.in(SysRole.Fields.code, roles);
        return this.findAll(q);
    }

    @Transactional
    public void createDefault() {
        long count = roleDao.countByCode(SysRole.DEFAULT_ROLE);
        if (count > 0) {
            return;
        }
        SysRole sysRole = new SysRole();
        sysRole.setId(SysRole.DEFAULT_ROLE);
        sysRole.setCode(SysRole.DEFAULT_ROLE);
        sysRole.setName("系统默认角色");
        sysRole.setStatus(CommonStatus.ENABLE);

        roleDao.save(sysRole);
    }
}
