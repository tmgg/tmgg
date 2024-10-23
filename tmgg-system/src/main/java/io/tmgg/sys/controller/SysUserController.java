
package io.tmgg.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.PasswdStrength;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ExcelExportTool;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.sys.dto.GrantPermDto;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.org.enums.OrgType;
import io.tmgg.sys.org.service.SysOrgService;
import io.tmgg.sys.service.SysConfigService;
import io.tmgg.sys.service.SysUserService;
import io.tmgg.sys.user.param.SysUserParam;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@RequestMapping("sysUser")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysConfigService configService;

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysHttpSessionService sm;

    @HasPermission
    @GetMapping("page")
    public AjaxResult page(String orgId, String keyword, @PageableDefault(sort = SysUser.FIELD_UPDATE_TIME, direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        Page<SysUser> page = sysUserService.findAll(orgId, keyword, pageable);
        sysUserService.fillRoleName(page);
        return AjaxResult.ok().data(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody SysUser input) throws Exception {
        boolean isNew = input.isNew();
        String inputOrgId = input.getDeptId();
        SysOrg org = sysOrgService.findOne(inputOrgId);
        if (org.getType() == OrgType.UNIT) {
            input.setUnitId(inputOrgId);
            input.setDeptId(null);
        } else {
            SysOrg unit = sysOrgService.findParentUnit(org);
            Assert.notNull(unit, "部门%s没有所属单位".formatted(org.getName()));
            input.setUnitId(unit.getId());
        }

        SysUser sysUser = sysUserService.saveOrUpdate(input);
        sm.forceExistBySubjectId(sysUser.getId());

        if(isNew){
            return AjaxResult.ok().msg("添加成功,密码：" + configService.getDefaultPassWord());
        }

        return AjaxResult.ok();
    }


    @HasPermission
    @GetMapping("delete")
    public AjaxResult delete(String id) {
        sysUserService.delete(id);
        return AjaxResult.ok();
    }


    /**
     * 检查密码强度
     *
     * @param password
     */
    @GetMapping("pwdStrength")
    public AjaxResult pwdStrength(String password) {
        if (password == null) {
            return AjaxResult.err().msg("请输入密码");
        }

        PasswdStrength.PASSWD_LEVEL level = PasswdStrength.getLevel(password);

        switch (level) {
            case EASY:
                return AjaxResult.err().msg("密码太简单");
            case MIDIUM:
                return AjaxResult.err().msg("密码强度一般");
        }

        return AjaxResult.ok().data(level);
    }

    @PostMapping("updatePwd")
    @Remark("修改密码")
    public AjaxResult updatePwd(String password, String newPassword) {
        String userId = SecurityUtils.getSubject().getId();
        sysUserService.updatePwd(userId, password, newPassword);
        sm.forceExistBySubjectId(userId);
        return AjaxResult.ok();
    }





    /**
     * 重置密码
     */
    @HasPermission
    @PostMapping("resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUserParam sysUserParam) {
        sysUserService.resetPwd(sysUserParam.getId());
        sm.forceExistBySubjectId(sysUserParam.getId());
        return AjaxResult.ok().msg("重置成功,新密码为：" + configService.getDefaultPassWord());
    }


    @HasPermission
    @GetMapping("export")
    public void export(HttpServletResponse response) throws IOException {
        List<SysUser> list = sysUserService.findAll();

        sysUserService.fillRoleName(list);


        Map<String, Function<SysUser,Object>> columns = new LinkedHashMap<>();
        columns.put("姓名", SysUser::getName);
        columns.put("账号", SysUser::getAccount);
        columns.put("手机号", SysUser::getPhone);
        columns.put("部门", SysUser::getDeptLabel);
        columns.put("单位",SysUser::getUnitLabel);
        columns.put("角色", SysUser::getRoleNames);

        ExcelExportTool.exportTable("用户列表.xlsx", columns, list, response);
    }


    @GetMapping("options")
    public AjaxResult options(String searchText) {
        JpaQuery<SysUser> query = new JpaQuery<>();

        if (searchText != null) {
            query.like("name", "%" + searchText.trim() + "%");
        }

        // 权限过滤
        Collection<String> orgIds = SecurityUtils.getSubject().getOrgPermissions();
        if (CollUtil.isNotEmpty(orgIds)) {
            query.or(q -> {
                q.in(SysUser.Fields.unitId, orgIds);
                q.in(SysUser.Fields.deptId, orgIds);
            });

        }

        Page<SysUser> page = sysUserService.findAll(query, PageRequest.of(0, 200));

        List<Option> options = Option.convertList(page.getContent(), BaseEntity::getId, t -> {
            if (t.getDeptLabel() != null) {
                return t.getName() + " (" + t.getDeptLabel() + ")";
            }

            return t.getName();
        });


        return AjaxResult.ok().data(options);
    }


    /**
     * 拥有数据
     */
    @GetMapping("getPermInfo")
    public AjaxResult getPermInfo(String id) {
        GrantPermDto permInfo = sysUserService.getPermInfo(id);
        return AjaxResult.ok().data(permInfo);
    }


    /**
     * 授权数据
     */
    @PostMapping("grantPerm")
    public AjaxResult grantPerm(@Valid @RequestBody GrantPermDto param) {
        sysUserService.grantPerm(param.getId(), param.getRoleIds(),param.getDataPermType(),  param.getOrgIds());

        sm.forceExistBySubjectId(param.getId());
        return AjaxResult.ok();
    }

    @GetMapping("tree")
    public AjaxResult tree() {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> orgList = sysOrgService.findByLoginUser(subject, null, true);
        if (orgList.isEmpty()) {
            return AjaxResult.ok().data(Collections.emptyList());
        }

        Collection<String> orgPermissions = subject.getOrgPermissions();
        List<SysUser> userList = sysUserService.findByUnit(orgPermissions);

        List<TreeOption> tree = orgList.stream().map(o -> new TreeOption("[" + o.getType().getMessage() + "] " + o.getName(), o.getId(), o.getPid())).collect(Collectors.toList());

        List<TreeOption> userTree = userList.stream().map(u -> new TreeOption(u.getName(), u.getId(), StrUtil.emptyToDefault(u.getDeptId(), u.getUnitId()))).collect(Collectors.toList());

        tree.addAll(userTree);

        tree = TreeOption.convertTree(tree);

        return AjaxResult.ok().data(tree);
    }



}
