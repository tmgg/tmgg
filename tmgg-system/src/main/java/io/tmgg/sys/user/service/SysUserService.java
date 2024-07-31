
package io.tmgg.sys.user.service;

import io.tmgg.lang.CodeException;
import io.tmgg.lang.PasswordTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.exports.UserLabelQuery;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.consts.service.SysConfigService;
import io.tmgg.sys.file.service.SysFileService;
import io.tmgg.sys.org.dao.SysOrgDao;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.role.dao.SysRoleDao;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.user.UserChangedEvent;
import io.tmgg.sys.user.controller.GrantDataParam;
import io.tmgg.sys.user.dao.SysUserDao;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.sys.user.enums.DataPermType;
import io.tmgg.sys.user.enums.SysUserExceptionEnum;
import io.tmgg.sys.user.factory.SysUserFactory;
import io.tmgg.sys.user.param.SysUserParam;
import io.tmgg.web.enums.AdminType;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SysUserService extends BaseService<SysUser> implements UserLabelQuery {


    @Resource
    @Lazy
    private SysFileService sysFileService;

    @Resource
    private SysUserDao sysUserDao;


    @Resource
    private SysRoleDao roleDao;


    @Resource
    private SysOrgDao sysOrgDao;

    @Resource
    private SysConfigService sysConfigService;

    public List<SysUser> findByOrg(Collection<String> org) {
        JpaQuery<SysUser> query = new JpaQuery<>();
        query.in(SysUser.Fields.orgId, org);
        return sysUserDao.findAll(query, Sort.by(SysUser.Fields.name));
    }

    public SysUser findByAccount(String account) {
        JpaQuery<SysUser> query = new JpaQuery<>();
        query.eq(SysUser.Fields.account, account);
        SysUser user = sysUserDao.findOne(query);
        return user;
    }


    public SysUser findByPhone(String phoneNumber) {
        JpaQuery<SysUser> query = new JpaQuery<>();
        query.eq(SysUser.Fields.phone, phoneNumber);
        SysUser user = sysUserDao.findOne(query);
        return user;
    }


    public Set<String> getUserRoleIdList(String userId) {
        SysUser user = sysUserDao.findOne(userId);
        Set<SysRole> roles = user.getRoles();
        return roles.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }


    @Transactional
    public void grantRole(String userId, List<String> roleIds) {
        SysUser user = sysUserDao.findOne(userId);
        List<SysRole> newRoles = roleDao.findAllById(roleIds);

        Set<SysRole> roles = user.getRoles();
        roles.clear();
        roles.addAll(newRoles);
    }


    public boolean isPasswordSameAsDefault(String dbPwd) {
        String defaultPassWord = SpringUtil.getBean(SysConfigService.class).getDefaultPassWord();

        // 是否和默认密码相同

        return PasswordTool.checkpw(defaultPassWord, dbPwd);
    }


    public void fillRoleName(Iterable<SysUser> list) {
        for (SysUser user : list) {
            Set<SysRole> roles = user.getRoles();
            user.setRoleNames(roles.stream().map(SysRole::getName).collect(Collectors.toList()));
            user.setRoleIds(roles.stream().map(SysRole::getId).collect(Collectors.toList()));
        }
    }


    public SysUser getUserByAccount(String account) {
        JpaQuery<SysUser> queryWrapper = new JpaQuery<>();
        queryWrapper.eq(SysUser.Fields.account, account);
        queryWrapper.ne(SysUser.Fields.status, CommonStatus.DELETED);
        return this.findOne(queryWrapper);
    }


    public Page<SysUser> findAll(SysUserParam param, Pageable pageable) throws SQLException {
        JpaQuery<SysUser> query = new JpaQuery<>();

        if (ObjectUtil.isNotEmpty(param.getOrgId())) {
            query.any(q -> {
                q.eq(SysUser.Fields.orgId, param.getOrgId());
                q.eq(SysUser.Fields.deptId, param.getOrgId());

            });
        }


        if (StrUtil.isNotEmpty(param.getName()))
            query.like(SysUser.Fields.name, param.getName());
        if (StrUtil.isNotEmpty(param.getPhone()))
            query.like(SysUser.Fields.phone, param.getPhone());
        if (StrUtil.isNotEmpty(param.getAccount()))
            query.like(SysUser.Fields.account, param.getAccount());


        //查询非删除状态，排除超级管理员
        query.ne(SysUser.Fields.adminType, AdminType.SUPER_ADMIN);


        Subject subject = SecurityUtils.getSubject();

        Page<SysUser> page = sysUserDao.findAll(query, pageable);
        return page;
    }


    @Transactional(rollbackFor = Exception.class)
    public void add(SysUserParam param) {
        checkParam(param, false);

        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(param, sysUser);
        SysUserFactory.fillAddCommonUserInfo(sysUser);
        if (ObjectUtil.isNotEmpty(param.getPassword())) {
            sysUser.setPassword(PasswordTool.encode(param.getPassword()));
        }
        sysUser = this.save(sysUser);


        SpringUtil.getApplicationContext().publishEvent(new UserChangedEvent(this));

        this.grantRole(sysUser.getId(), param.getRoleIds());
    }


    @Transactional
    public void delete(String id) {
        SysUser sysUser = sysUserDao.findOne(id);
        //不能删除超级管理员
        if (AdminType.SUPER_ADMIN == sysUser.getAdminType()) {
            throw new CodeException(SysUserExceptionEnum.USER_CAN_NOT_DELETE_ADMIN);
        }
        try {
            sysUserDao.delete(sysUser);
        } catch (Exception e) {
            throw new IllegalStateException("用户已被引用，无法删除。可以尝试禁用该用户: " + sysUser.getName());
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void edit(SysUserParam param) {
        checkParam(param, true);

        // 账号
        SysUser user = sysUserDao.findOne(param.getId());
        user.setAccount(param.getAccount());
        user.setName(param.getName());
        user.setStatus(param.getStatus());
        user.setPhone(param.getPhone());
        user.setOrgId(param.getOrgId());
        user.setDeptId(param.getDeptId());
        this.save(user);

        // 角色
        this.grantRole(user.getId(), param.getRoleIds());
    }


    public void updatePwd(String userId, String password, String newPassword) {
        Assert.hasText(password, "请输入密码");
        Assert.hasText(newPassword, "请输入新密码");
        SysUser sysUser = this.findOne(userId);

        Assert.state(!newPassword.equals(password), "新密码与原密码相同，请检查newPassword参数");
        Assert.state(PasswordTool.checkpw(password, sysUser.getPassword()), "原密码错误");

        PasswordTool.validateStrength(newPassword);

        sysUser.setPassword(PasswordTool.encode(newPassword));
        this.save(sysUser);
    }


    @Override
    public synchronized String getNameById(String userId) {
        if (userId == null || sysUserDao == null) {
            return null;
        }

        return sysUserDao.getNameById(userId);
    }


    public List<String> ownRole(SysUserParam sysUserParam) {
        SysUser sysUser = this.findOne(sysUserParam.getId());
        return sysUser.getRoles().stream().map(SysRole::getId).collect(Collectors.toList());
    }


    @Transactional
    public void resetPwd(String id) {
        String password = sysConfigService.getDefaultPassWord();
        this.resetPwd(id, password);
    }

    @Transactional
    public void resetPwd(String id, String plainPassword) {
        SysUser sysUser = this.findOne(id);
        PasswordTool.validateStrength(plainPassword);

        sysUser.setPassword(PasswordTool.encode(plainPassword));
        this.save(sysUser);
    }


    public void updateAvatar(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        String avatar = sysUserParam.getAvatar();
        sysFileService.assertFile(avatar);
        sysUser.setAvatar(avatar);
        this.save(sysUser);

    }


    public SysUser getUserById(String userId) {
        SysUser sysUser = this.findOne(userId);
        if (ObjectUtil.isNull(sysUser)) {
            throw new CodeException(SysUserExceptionEnum.USER_NOT_EXIST);
        }
        return sysUser;
    }


    public List<String> getAllUserIdList() {
        List<String> resultList = CollectionUtil.newArrayList();
        JpaQuery<SysUser> queryWrapper = new JpaQuery<>();
        queryWrapper.ne(SysUser.Fields.adminType, AdminType.SUPER_ADMIN);
        this.findAll(queryWrapper).forEach(sysUser -> resultList.add(sysUser.getId()));
        return resultList;
    }


    /**
     * 校验参数，检查是否存在相同的账号
     */
    private void checkParam(SysUserParam param, boolean isExcludeSelf) {
        String id = param.getId();
        String account = param.getAccount();
        JpaQuery<SysUser> queryWrapper = new JpaQuery<>();
        queryWrapper.eq(SysUser.Fields.account, account)
                .ne(SysUser.Fields.status, CommonStatus.DELETED);
        //是否排除自己，如果是则查询条件排除自己id
        if (isExcludeSelf) {
            queryWrapper.ne("id", id);
        }
        long countByAccount = this.count(queryWrapper);
        //大于等于1个则表示重复
        if (countByAccount >= 1) {
            throw new CodeException(SysUserExceptionEnum.USER_ACCOUNT_REPEAT);
        }
    }


    /**
     * 获取系统用户
     */
    private SysUser querySysUser(SysUserParam sysUserParam) {
        SysUser sysUser = this.findOne(sysUserParam.getId());
        if (ObjectUtil.isNull(sysUser)) {
            throw new CodeException(SysUserExceptionEnum.USER_NOT_EXIST);
        }
        return sysUser;
    }

    public List<SysUser> findValid() {
        JpaQuery<SysUser> q = new JpaQuery<>();

        q.eq(SysUser.Fields.status, CommonStatus.ENABLE);

        return sysUserDao.findAll(q);
    }

    public boolean hasUserUnderOrg(String orgId) {
        JpaQuery<SysUser> q = new JpaQuery<>();

        q.eq(SysUser.Fields.orgId, orgId);

        return sysUserDao.count(q) > 0;
    }

    // 数据范围
    public Collection<String> getLoginDataScope(String userId) {
        SysUser user = sysUserDao.findOne(userId);
        DataPermType dataPermType = user.getDataPermType();
        if (dataPermType == null) {
            dataPermType = DataPermType.ORG_AND_CHILDREN;
        }


        // 超级管理员返回所有
        if (user.getAdminType() == AdminType.SUPER_ADMIN || dataPermType == DataPermType.ALL) {
            List<SysOrg> all = sysOrgDao.findAll();
            return all.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        }

        String orgId = user.getOrgId();
        switch (dataPermType) {
            case ORG_ONLY:
                return orgId == null ? Collections.emptyList() : Collections.singletonList(orgId);
            case ORG_AND_CHILDREN:
                return sysOrgDao.findChildIdListWithSelfById(orgId, true);
            case CUSTOM:
                return user.getOrgDataScope().stream().map(o -> o.getId()).collect(Collectors.toList());
        }

        throw new IllegalStateException("有未处理的类型" + dataPermType);

    }

    public List<String> ownData(String id) {
        SysUser user = this.findOne(id);
        List<SysOrg> orgDataScope = user.getOrgDataScope();
        return orgDataScope.stream().map(BaseEntity::getId).collect(Collectors.toList());
    }

    @Transactional
    public void grantData(@RequestBody @Validated GrantDataParam param) {
        SysUser user = this.findOne(param.getId());
        List<SysOrg> orgs = sysOrgDao.findAllById(param.getGrantOrgIdList());
        user.setOrgDataScope(orgs);
        user.setDataPermType(param.getDataPermType());
    }


    public List<SysUser> findByRole(SysRole role) {
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.isMember(SysUser.Fields.roles, role);

        return this.findAll(q);
    }


    public List<SysUser> findByRoleCode(String code) {
        SysRole role = roleDao.findByCode(code);
        Assert.state(role != null, "编码为" + code + "的角色不存在");

        return this.findByRole(role);
    }

    public List<SysUser> findByRoleId(String id) {
        SysRole role = roleDao.findOne(id);
        Assert.state(role != null, "角色不存在");

        return this.findByRole(role);
    }


}
