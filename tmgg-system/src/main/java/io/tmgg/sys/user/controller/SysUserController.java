
package io.tmgg.sys.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.PasswdStrength;
import cn.hutool.core.util.StrUtil;
import cn.moon.lang.web.ServletTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.excel.Col;
import io.tmgg.lang.excel.ExcelWrap;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.sys.consts.service.SysConfigService;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.org.service.SysOrgService;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.sys.user.param.SysUserParam;
import io.tmgg.sys.user.service.SysUserService;
import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户
 */
@RestController
@RequestMapping("sysUser")
@BusinessLog("用户")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysConfigService configService;

    @Resource
    private SysOrgService sysOrgService;


    @BusinessLog("查看列表")
    @HasPermission
    @GetMapping("page")
    public AjaxResult page(SysUserParam sysUserParam, @PageableDefault(sort = SysUser.FIELD_UPDATE_TIME ,direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        Page<SysUser> page = sysUserService.findAll(sysUserParam, pageable);
        sysUserService.fillRoleName(page);
        return AjaxResult.success(null, page);
    }

    @HasPermission
    @PostMapping("add")
    @BusinessLog("系统用户_增加")
    public AjaxResult add(@RequestBody SysUserParam sysUserParam) {
        sysUserService.add(sysUserParam);
        return AjaxResult.success("添加用户成功，新密码为" + configService.getDefaultPassWord(), null);
    }


    @HasPermission
    @PostMapping("edit")
    @BusinessLog("系统用户_编辑")
    public AjaxResult edit(@RequestBody SysUserParam sysUserParam) {
        sysUserService.edit(sysUserParam);
        SecurityUtils.refresh(sysUserParam.getId());
        return AjaxResult.success();
    }


    @HasPermission
    @GetMapping("delete")
    @BusinessLog("系统用户_删除")
    public AjaxResult delete(String id) {
        sysUserService.delete(id);
        return AjaxResult.success();
    }


    /**
     * 检查密码强度
     * @param password
     *
     */
    @GetMapping("pwdStrength")
    public AjaxResult pwdStrength(String password) {
        if(password == null){
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

    /**
     * 修改密码
     */
    @PostMapping("updatePwd")
    @BusinessLog("系统用户_修改密码")
    public AjaxResult updatePwd(String password, String newPassword) {
        String userId = SecurityUtils.getSubject().getId();

        sysUserService.updatePwd(userId, password, newPassword);
        SecurityUtils.logout(userId);
        return AjaxResult.success();
    }

    /**
     * 拥有角色
     */
    @GetMapping("ownRole")
    public AjaxResult ownRole(SysUserParam sysUserParam) {
        return AjaxResult.success(sysUserService.ownRole(sysUserParam));
    }


    /**
     * 重置密码
     */
    @HasPermission
    @PostMapping("resetPwd")
    @BusinessLog("系统用户_重置密码")
    public AjaxResult resetPwd(@RequestBody  SysUserParam sysUserParam) {
        sysUserService.resetPwd(sysUserParam.getId());
        return AjaxResult.success("重置成功,新密码为：" + configService.getDefaultPassWord(), null);
    }

    /**
     * 修改头像
     */
    @PostMapping("updateAvatar")
    @BusinessLog("系统用户_修改头像")
    public AjaxResult updateAvatar(@RequestBody  SysUserParam sysUserParam) {
        sysUserService.updateAvatar(sysUserParam);
        return AjaxResult.success();
    }


    @HasPermission
    @GetMapping("detail")
    public AjaxResult detail(String id) {
        SysUser user = sysUserService.findOne(id);

        sysUserService.fillRoleName(CollUtil.newArrayList(user));

        return AjaxResult.success(user);
    }

    @HasPermission
    @GetMapping("export")
    public void export(HttpServletResponse response) throws IOException {
        List<SysUser> list = sysUserService.findAll();

        sysUserService.fillRoleName(list);


        ExcelWrap excel = new ExcelWrap();
        Col<SysUser>[] cols = new Col[]{
                Col.builder().title("姓名").dataIndex("name").build(),
                Col.builder().title("账号").dataIndex("account").build(),
                Col.builder().title("手机号").dataIndex("phone").build(),
                Col.<SysUser>builder().title("部门").render(SysUser::getDeptLabel).build(),
                Col.<SysUser>builder().title("单位").render(SysUser::getOrgLabel).build(),
                Col.<SysUser>builder().title("角色").render(SysUser::getRoleNames).build(),
        };

        excel.addBeanList(list, cols);

        ServletTool.setDownloadFileHeader("用户列表.xlsx", response);
        excel.writeTo(response.getOutputStream());
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
            query.any(q -> {
                q.in(SysUser.Fields.orgId, orgIds);
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


        return AjaxResult.success(options);
    }


    /**
     * 拥有数据
     */
    @GetMapping("ownData")
    public AjaxResult ownData(String id) {
        return AjaxResult.success(sysUserService.ownData(id));
    }


    /**
     * 授权数据
     */

    @HasPermission(title = "授权数据")
    @PostMapping("grantData")
    public AjaxResult grantData(@Valid @RequestBody GrantDataParam param) {
        sysUserService.grantData(param);

        SecurityUtils.refresh(param.getId());

        return AjaxResult.success();
    }

    @GetMapping("tree")
    public AjaxResult tree() {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> orgList = sysOrgService.findByLoginUser(subject, null, true);
        if (orgList.isEmpty()) {
            return AjaxResult.success(Collections.emptyList());
        }

        Collection<String> orgPermissions = subject.getOrgPermissions();
        List<SysUser> userList = sysUserService.findByOrg(orgPermissions);

        List<TreeOption> tree = orgList.stream().map(o -> new TreeOption("[" + o.getType().getMessage() + "] " + o.getBestName(), o.getId(), o.getPid())).collect(Collectors.toList());

        List<TreeOption> userTree = userList.stream().map(u -> new TreeOption(u.getName(), u.getId(), StrUtil.emptyToDefault(u.getDeptId(), u.getOrgId()))).collect(Collectors.toList());

        tree.addAll(userTree);

        tree = TreeOption.convertTree(tree);

        return AjaxResult.success(tree);
    }


}
