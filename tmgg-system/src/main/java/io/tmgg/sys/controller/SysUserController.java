
package io.tmgg.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.PasswdStrength;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.excel.Col;
import io.tmgg.lang.excel.ExcelWrap;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.sys.app.service.SysConfigService;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.org.service.SysOrgService;
import io.tmgg.sys.service.SysUserService;
import io.tmgg.sys.user.controller.GrantDataParam;
import io.tmgg.sys.user.param.SysUserParam;
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

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
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


    @HasPermission
    @GetMapping("page")
    public AjaxResult page(String orgId, String keyword, @PageableDefault(sort = SysUser.FIELD_UPDATE_TIME ,direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        Page<SysUser> page = sysUserService.findAll(orgId, keyword, pageable);
        sysUserService.fillRoleName(page);
        return AjaxResult.ok().data( page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult add(@RequestBody SysUser sysUser) throws Exception {
        sysUserService.saveOrUpdate(sysUser);

        return AjaxResult.ok().msg("添加用户成功，新密码为" + configService.getDefaultPassWord());
    }


    @HasPermission
    @PostMapping("edit")
    public AjaxResult edit(@RequestBody SysUserParam sysUserParam) {
        sysUserService.edit(sysUserParam);
        SecurityUtils.refresh(sysUserParam.getId());
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
        return AjaxResult.ok();
    }


    @GetMapping("ownRole")
    public AjaxResult ownRole(SysUserParam sysUserParam) {
        return AjaxResult.ok().data(sysUserService.ownRole(sysUserParam));
    }


    /**
     * 重置密码
     */
    @HasPermission
    @PostMapping("resetPwd")
    public AjaxResult resetPwd(@RequestBody  SysUserParam sysUserParam) {
        sysUserService.resetPwd(sysUserParam.getId());
        return AjaxResult.ok().msg("重置成功,新密码为：" + configService.getDefaultPassWord());
    }



    @HasPermission
    @GetMapping("detail")
    public AjaxResult detail(String id) {
        SysUser user = sysUserService.findOne(id);

        sysUserService.fillRoleName(CollUtil.newArrayList(user));

        return AjaxResult.ok().data(user);
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
                Col.<SysUser>builder().title("单位").render(SysUser::getUnitId).build(),
                Col.<SysUser>builder().title("角色").render(SysUser::getRoleNames).build(),
        };

        excel.addBeanList(list, cols);

        // TODO
      //  ServletTool.setDownloadFileHeader("用户列表.xlsx", response);
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
    @GetMapping("ownData")
    public AjaxResult ownData(String id) {
        return AjaxResult.ok().data(sysUserService.ownData(id));
    }


    /**
     * 授权数据
     */
    @PostMapping("grantData")
    public AjaxResult grantData(@Valid @RequestBody GrantDataParam param) {
        sysUserService.grantData(param);

        SecurityUtils.refresh(param.getId());

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

        List<TreeOption> tree = orgList.stream().map(o -> new TreeOption("[" + o.getType().getMessage() + "] " + o.getBestName(), o.getId(), o.getPid())).collect(Collectors.toList());

        List<TreeOption> userTree = userList.stream().map(u -> new TreeOption(u.getName(), u.getId(), StrUtil.emptyToDefault(u.getDeptId(), u.getUnitId()))).collect(Collectors.toList());

        tree.addAll(userTree);

        tree = TreeOption.convertTree(tree);

        return AjaxResult.ok().data(tree);
    }


}