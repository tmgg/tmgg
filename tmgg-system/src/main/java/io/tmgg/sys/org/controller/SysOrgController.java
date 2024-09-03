
package io.tmgg.sys.org.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.org.enums.OrgType;
import io.tmgg.sys.org.service.SysOrgService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
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
    public AjaxResult saveOrUpdate(@RequestBody  SysOrg sysOrg) {
        sysOrgService.saveOrUpdate(sysOrg);


        return AjaxResult.ok().msg("保存机构成功");
    }

    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(@RequestBody SysOrg sysOrg) {
        sysOrgService.deleteById(sysOrg.getId());
        return AjaxResult.ok();
    }

    @HasPermission
    @GetMapping("detail")
    public AjaxResult detail(String id) {
        SysOrg org = sysOrgService.findOne(id);
        return AjaxResult.ok().data(org);
    }

    @HasPermission
    @GetMapping("enableAll")
    public AjaxResult enableAll(String id) {
        sysOrgService.toggleAllStatus(id,true);
        return AjaxResult.ok();
    }

    @HasPermission
    @GetMapping("disableAll")
    public AjaxResult disableAll(String id) {
        sysOrgService.toggleAllStatus(id,false);
        return AjaxResult.ok();
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

        return AjaxResult.ok().data(tree);
    }

}
