
package io.tmgg.sys.org.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.org.enums.OrgType;
import io.tmgg.sys.org.service.SysOrgService;
import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织机构控制器
 */
@RestController
@RequestMapping("sysOrg")
@Slf4j
public class SysOrgController {

    @Resource
    private SysOrgService sysOrgService;


    @HasPermission
    @PostMapping("save")
    @BusinessLog("组织机构_保存")
    public AjaxResult saveOrUpdate(@RequestBody  SysOrg sysOrg) {
        sysOrgService.saveOrUpdate(sysOrg);

        // 否则需要二次登录
        SecurityUtils.refresh(SecurityUtils.getSubject().getId());

        return AjaxResult.success();
    }

    @HasPermission
    @PostMapping("delete")
    @BusinessLog("组织机构_删除")
    public AjaxResult delete(@RequestBody SysOrg sysOrg) {
        sysOrgService.deleteById(sysOrg.getId());
        return AjaxResult.success();
    }

    @HasPermission
    @GetMapping("detail")
    @BusinessLog("组织机构_查看")
    public AjaxResult detail(String id) {
        return AjaxResult.success(sysOrgService.findOne(id));
    }

    @HasPermission
    @GetMapping("enableAll")
    @BusinessLog("组织机构_启用所有")
    public AjaxResult enableAll(String id) {
        sysOrgService.toggleAllStatus(id,true);
        return AjaxResult.success();
    }

    @HasPermission
    @GetMapping("disableAll")
    @BusinessLog("组织机构_禁用所有")
    public AjaxResult disableAll(String id) {
        sysOrgService.toggleAllStatus(id,false);
        return AjaxResult.success();
    }


    /***
     *
     * @param type
     * @param showAll 是否包含禁用的
     *
     */
    @GetMapping("tree")
    public AjaxResult tree(OrgType type, Boolean filterDept, @RequestParam(defaultValue = "false") boolean showAll) {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> list = sysOrgService.findByLoginUser(subject, type,showAll);

        if(filterDept != null && filterDept){
            list = list.stream().filter(o->!o.isDept()).collect(Collectors.toList());
        }

        List<TreeOption> treeList = list.stream().map(o -> {
            TreeOption treeOption = new TreeOption();
            treeOption.setTitle(o.getName());
            treeOption.setKey(o.getId());
            treeOption.setParentKey(o.getPid());

            if(o.getStatus() != CommonStatus.ENABLE){
                treeOption.setTitle(treeOption.getTitle() +" ["+ o.getStatus().getMessage() +"]");
            }

            return treeOption;
        }).collect(Collectors.toList());

        List<TreeOption> tree = TreeOption.convertTree(treeList);

        return AjaxResult.success(tree);
    }

}
