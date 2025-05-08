
package io.tmgg.modules.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.PasswdStrength;
import cn.hutool.core.util.StrUtil;
import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.Table;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.lang.poi.ExcelExportTool;
import io.tmgg.modules.sys.dto.GrantPermDto;
import io.tmgg.modules.sys.entity.OrgType;
import io.tmgg.modules.sys.entity.SysOrg;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.modules.sys.service.SysOrgService;
import io.tmgg.modules.sys.service.SysUserService;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    @Data
    public static class QueryParam extends CommonQueryParam {
        String orgId;
        String roleId;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody QueryParam param, @PageableDefault(sort = SysUser.FIELD_UPDATE_TIME, direction = Sort.Direction.DESC) Pageable pageable, HttpServletResponse resp) throws SQLException, IOException {
        Page<SysUser> page = sysUserService.findAll(param.getOrgId(), param.getRoleId(), param.getKeyword(), pageable);
        sysUserService.fillRoleName(page);

//        if(param.getExportExcel()){
//            sysUserService.exportExcel(page.getContent(), "用户列表.xlsx", resp);
//            return null;
//        }

        return AjaxResult.ok().data(page).cls(SysUser.class);
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

        if (isNew) {
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
        if (StrUtil.isEmpty(password)) {
            return AjaxResult.err().msg("请输入密码");
        }

        PasswdStrength.PASSWD_LEVEL level = PasswdStrength.getLevel(password);

        if (level == PasswdStrength.PASSWD_LEVEL.EASY) {
            return AjaxResult.err().msg("密码强度太低");
        }

        return AjaxResult.ok().data(level);
    }

    @Data
    public static class UpdatePwdParam{
        String newPassword;
    }

    @PostMapping("updatePwd")
    @HasPermission(label = "修改密码")
    public AjaxResult updatePwd(@RequestBody UpdatePwdParam param) {
        String userId = SecurityUtils.getSubject().getId();
        String newPassword = param.getNewPassword();
        sysUserService.updatePwd(userId,  newPassword);
        sm.forceExistBySubjectId(userId);
        return AjaxResult.ok();
    }


    @HasPermission(label = "重置密码")
    @PostMapping("resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        sysUserService.resetPwd(user.getId());
        sm.forceExistBySubjectId(user.getId());
        String defaultPassWord = configService.getDefaultPassWord();
        return AjaxResult.ok().msg("重置成功,新密码为：" + defaultPassWord).data("新密码：" + defaultPassWord);
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
            query.addSubOr(q -> {
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
        sysUserService.grantPerm(param.getId(), param.getRoleIds(), param.getDataPermType(), param.getOrgIds());

        sm.forceExistBySubjectId(param.getId());
        return AjaxResult.ok();
    }

    /**
     * 用户树
     * 机构刷下面增加用户节点
     * @return
     */
    @GetMapping("tree")
    public AjaxResult tree() {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> orgList = sysOrgService.findByLoginUser(subject, true, false);
        if (orgList.isEmpty()) {
            return AjaxResult.ok().data(Collections.emptyList());
        }

        Collection<String> orgPermissions = subject.getOrgPermissions();
        List<SysUser> userList = sysUserService.findByUnit(orgPermissions);

        List<TreeOption> tree = orgList.stream().map(o -> new TreeOption(o.getName(), o.getId(), o.getPid())).collect(Collectors.toList());

        List<TreeOption> userTree = userList.stream().map(u -> new TreeOption(u.getName(), u.getId(), StrUtil.emptyToDefault(u.getDeptId(), u.getUnitId()))).collect(Collectors.toList());

        tree.addAll(userTree);

        tree = TreeOption.convertTree(tree);

        return AjaxResult.ok().data(tree);
    }


}
