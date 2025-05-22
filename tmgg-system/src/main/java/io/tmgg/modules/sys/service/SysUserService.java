
package io.tmgg.modules.sys.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.lang.PasswordTool;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.web.persistence.exports.UserLabelQuery;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.sys.dao.SysOrgDao;
import io.tmgg.modules.sys.dao.SysRoleDao;
import io.tmgg.modules.sys.dao.SysUserDao;
import io.tmgg.modules.sys.dto.GrantPermDto;
import io.tmgg.modules.sys.entity.DataPermType;
import io.tmgg.modules.sys.entity.SysOrg;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.web.CodeAssert;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SysUserService extends BaseService<SysUser> implements UserLabelQuery {

    public static final int CODE_PWD_ERR = 4011;


    @Resource
    private SysUserDao sysUserDao;


    @Resource
    private SysRoleDao roleDao;


    @Resource
    private SysOrgDao sysOrgDao;

    @Resource
    private SysConfigService sysConfigService;


    @Resource
    private SysHttpSessionService sm;


    public SysUser checkLogin(String account, String password) {
        SysUser sysUser = sysUserDao.findByAccount(account);

        Assert.hasText(account, "账号不能为空");
        Assert.hasText(password, "密码不能为空");
        Assert.notNull(sysUser, "账号不存在");

        Assert.state(sysUser.getEnabled(), "账号已禁用");
        String passwordBcrypt = sysUser.getPassword();
        Assert.hasText(passwordBcrypt, "账号未设置密码");



        boolean checkpw = PasswordTool.checkpw(password, passwordBcrypt);
        if (!checkpw) {
            log.info("登录密码错误,记录错误");
        }
        CodeAssert.state(checkpw, CODE_PWD_ERR,"账号或密码错误");



        // 多端登录检测
        if (!sysConfigService.getMultiDeviceLogin()) {
            // "您的账号已在其他地方登录"
            sm.forceExistBySubjectId(sysUser.getId());
        }

        return sysUser;
    }


    /**
     * 检查是否需要更新密码
     *
     * @param account
     * @param password
     * @return
     */
    public boolean checkNeedUpdatePwd(String account, String password) {
        String defaultPassWord = sysConfigService.getDefaultPassWord();
        return Objects.equals(password, defaultPassWord);
    }


    public List<SysUser> findByUnit(Collection<String> org) {
        JpaQuery<SysUser> query = new JpaQuery<>();
        query.in(SysUser.Fields.unitId, org);
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
        return this.findOne(queryWrapper);
    }


    public Page<SysUser> findAll(String orgId, String roleId, String searchText, Pageable pageable) throws SQLException {
        JpaQuery<SysUser> query = new JpaQuery<>();

        if (StrUtil.isNotEmpty(orgId)) {
            query.addSubOr(q -> {
                q.eq(SysUser.Fields.unitId, orgId);
                q.eq(SysUser.Fields.deptId, orgId);

            });
        }
        if (StrUtil.isNotEmpty(roleId)) {
            query.isMember(SysUser.Fields.roles, new SysRole(roleId));
        }

        if (StrUtil.isNotEmpty(searchText)) {
            query.addSubOr(q -> {
                q.like(SysUser.Fields.name, searchText);
                q.like(SysUser.Fields.phone, searchText);
                q.like(SysUser.Fields.account, searchText);
                q.like(SysUser.Fields.email, searchText);
            });
        }

        return sysUserDao.findAll(query, pageable);
    }

    @Override
    public SysUser saveOrUpdate(SysUser input, List<String> updateKeys) throws Exception {
        boolean isNew = input.isNew();
        if (isNew) {
            String password = sysConfigService.getDefaultPassWord();
            input.setPassword(PasswordTool.encode(password));
        }

        super.saveOrUpdate(input, updateKeys);
        return null;
    }



    @Transactional
    public void delete(String id) {
        SysUser sysUser = sysUserDao.findOne(id);
        try {
            sysUserDao.delete(sysUser);
        } catch (Exception e) {
            throw new IllegalStateException("用户已被引用，无法删除。可以尝试禁用该用户: " + sysUser.getName());
        }
    }


    @Transactional
    public void updatePwd(String userId, String newPassword) {
        Assert.hasText(newPassword, "请输入新密码");
        SysUser sysUser = this.findOne(userId);


        PasswordTool.validateStrength(newPassword);

        sysUser.setPassword(PasswordTool.encode(newPassword));
        sysUserDao.save(sysUser);
    }


    @Override
    public synchronized String getNameById(String userId) {
        if (userId == null || sysUserDao == null) {
            return null;
        }

        return sysUserDao.getNameById(userId);
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


    public List<String> getAllUserIdList() {
        List<String> resultList = CollectionUtil.newArrayList();
        JpaQuery<SysUser> queryWrapper = new JpaQuery<>();
        this.findAll(queryWrapper).forEach(sysUser -> resultList.add(sysUser.getId()));
        return resultList;
    }


    public List<SysUser> findValid() {
        return sysUserDao.findValid();
    }


    // 数据范围
    public Collection<String> getLoginDataScope(String userId) {
        SysUser user = sysUserDao.findOne(userId);
        DataPermType dataPermType = user.getDataPermType();
        if (dataPermType == null) {
            dataPermType = DataPermType.CHILDREN;
        }


        // 超级管理员返回所有
        if (dataPermType == DataPermType.ALL) {
            List<SysOrg> all = sysOrgDao.findAll();
            return all.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        }

        String orgId = user.getUnitId();
        switch (dataPermType) {
            case LEVEL:
                return orgId == null ? Collections.emptyList() : Collections.singletonList(orgId);
            case CHILDREN:
                return sysOrgDao.findChildIdListWithSelfById(orgId, true);
            case CUSTOM:
                return user.getDataPerms().stream().map(BaseEntity::getId).collect(Collectors.toList());
        }

        throw new IllegalStateException("有未处理的类型" + dataPermType);

    }

    public GrantPermDto getPermInfo(String id) {
        SysUser user = this.findOne(id);

        GrantPermDto p = new GrantPermDto();
        p.setId(user.getId());
        p.setDataPermType(user.getDataPermType());
        p.setOrgIds(user.getDataPerms().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        p.setRoleIds(user.getRoles().stream().map(BaseEntity::getId).collect(Collectors.toList()));

        return p;
    }

    @Transactional
    public void grantPerm(String id, List<String> roleIds, DataPermType dataPermType, List<String> orgIdList) {
        SysUser user = this.findOne(id);
        List<SysOrg> orgs = CollUtil.isNotEmpty(orgIdList) ? sysOrgDao.findAllById(orgIdList) : Collections.emptyList();
        user.setDataPerms(orgs);
        user.setDataPermType(dataPermType);


        List<SysRole> newRoles = roleDao.findAllById(roleIds);
        Set<SysRole> roles = user.getRoles();
        roles.clear();
        roles.addAll(newRoles);
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
