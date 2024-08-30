
package io.tmgg.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.CodeException;
import io.tmgg.lang.PasswordTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.exports.UserLabelQuery;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.app.service.SysConfigService;
import io.tmgg.sys.dao.SysUserDao;
import io.tmgg.sys.dto.GrantPermDto;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.org.dao.SysOrgDao;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.role.dao.SysRoleDao;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.user.enums.DataPermType;
import io.tmgg.sys.user.enums.SysUserExceptionEnum;
import io.tmgg.sys.user.param.SysUserParam;
import io.tmgg.web.enums.CommonStatus;
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


    @Resource
    private SysUserDao sysUserDao;


    @Resource
    private SysRoleDao roleDao;


    @Resource
    private SysOrgDao sysOrgDao;

    @Resource
    private SysConfigService sysConfigService;


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


    public Page<SysUser> findAll(String orgId, String keyword, Pageable pageable) throws SQLException {
        JpaQuery<SysUser> query = new JpaQuery<>();

        if (StrUtil.isNotEmpty(orgId)) {
            query.or(q -> {
                q.eq(SysUser.Fields.unitId, orgId);
                q.eq(SysUser.Fields.deptId, orgId);

            });
        }
        if(StrUtil.isNotEmpty(keyword)){
            query.or(q->{
                q.like(SysUser.Fields.name, keyword);
                q.like(SysUser.Fields.phone, keyword);
                q.like(SysUser.Fields.account, keyword);
                q.like(SysUser.Fields.email, keyword);
            });
        }

        return sysUserDao.findAll(query, pageable);
    }

    @Override
    public SysUser saveOrUpdate(SysUser input) throws Exception {
        boolean isNew = input.isNew();
        if (isNew) {
            String password = sysConfigService.getDefaultPassWord();
            input.setPassword(PasswordTool.encode(password));
            return baseDao.save(input);
        }





        SysUser old = baseDao.findOne(input);
        BeanUtil.copyProperties(input, old, CopyOptions.create()
                .setIgnoreProperties(ArrayUtil.append(BaseEntity.BASE_ENTITY_FIELDS,
                        SysUser.Fields.roles, SysUser.Fields.password, SysUser.Fields.dataPerms
                )));
        return  baseDao.save(old);

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
        ;
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



    // 数据范围
    public Collection<String> getLoginDataScope(String userId) {
        SysUser user = sysUserDao.findOne(userId);
        DataPermType dataPermType = user.getDataPermType();
        if (dataPermType == null) {
            dataPermType = DataPermType.ORG_AND_CHILDREN;
        }


        // 超级管理员返回所有
        if ( dataPermType == DataPermType.ALL) {
            List<SysOrg> all = sysOrgDao.findAll();
            return all.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        }

        String orgId = user.getUnitId();
        switch (dataPermType) {
            case ORG_ONLY:
                return orgId == null ? Collections.emptyList() : Collections.singletonList(orgId);
            case ORG_AND_CHILDREN:
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
    public void grantPerm( String id,List<String> roleIds, DataPermType dataPermType, List<String> orgIdList) {
        SysUser user = this.findOne(id);
        List<SysOrg> orgs = sysOrgDao.findAllById(orgIdList);
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
