
package io.tmgg.modules.sys.controller;

import cn.hutool.core.lang.Dict;
import io.tmgg.framework.session.SysHttpSession;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.DropEvent;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.modules.sys.entity.OrgType;
import io.tmgg.modules.sys.entity.SysOrg;
import io.tmgg.modules.sys.service.SysOrgService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public AjaxResult saveOrUpdate(@RequestBody SysOrg sysOrg, HttpSession session) {
        sysOrgService.saveOrUpdate(sysOrg);
        session.removeAttribute(SysHttpSession.SUBJECT_KEY);
        return AjaxResult.ok().msg("保存机构成功");
    }

    @HasPermission
    @PostMapping("delete")
    public AjaxResult delete(@RequestBody SysOrg sysOrg, HttpSession session) {
        sysOrgService.deleteById(sysOrg.getId());
        session.removeAttribute(SysHttpSession.SUBJECT_KEY);
        return AjaxResult.ok();
    }

    @GetMapping("detail")
    public AjaxResult detail(String id) {
        SysOrg org = sysOrgService.findOne(id);
        return AjaxResult.ok().data(org);
    }

    @HasPermission
    @GetMapping("enableAll")
    public AjaxResult enableAll(String id) {
        sysOrgService.toggleAllStatus(id, true);
        return AjaxResult.ok();
    }

    @HasPermission
    @GetMapping("disableAll")
    public AjaxResult disableAll(String id) {
        sysOrgService.toggleAllStatus(id, false);
        return AjaxResult.ok();
    }


    /***
     *
     * @param type
     * @param showAll 是否包含禁用的
     *
     */
    @GetMapping("tree")
    public AjaxResult tree(Integer type, Boolean filterDept, @RequestParam(defaultValue = "false") boolean showAll) {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> list = sysOrgService.findByLoginUser(subject, type, showAll);

        if (filterDept != null && filterDept) {
            list = list.stream().filter(o -> !o.isDept()).collect(Collectors.toList());
        }

        List<TreeOption> treeList = list.stream().map(o -> {
            TreeOption treeOption = new TreeOption();
            treeOption.setTitle(o.getName());
            treeOption.setKey(o.getId());
            treeOption.setParentKey(o.getPid());

            if (!o.getEnabled()) {
                treeOption.setTitle(treeOption.getTitle() + " [禁用]");
            }

            return treeOption;
        }).collect(Collectors.toList());

        List<TreeOption> tree = TreeOption.convertTree(treeList);

        return AjaxResult.ok().data(tree);
    }

    /**
     * 管理页面的树，包含禁用的
     *
     * @return
     */
    @GetMapping("pageTree")
    public AjaxResult pageTree() {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> list = sysOrgService.findByLoginUser(subject, null, true);


        List<Dict> treeList = list.stream().map(o -> {
            String title = o.getName();
            if (!o.getEnabled()) {
                title = title + " [禁用]";
            }

            Dict d = new Dict();
            d.set("title", title);
            d.set("key", o.getId());
            String pid = o.getPid();
            d.set("parentKey", pid);
            d.set("iconName", getIconByType(o.getType()));

            return d;
        }).collect(Collectors.toList());


        TreeManager<Dict> tm = TreeManager.of(treeList, "key", "parentKey");

        return AjaxResult.ok().data(tm.getTree());
    }

    /**
     * 用户管理界面等使用的机构树
     *
     * @return
     */
    @GetMapping("bizTree")
    public AjaxResult allTree() {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> list = sysOrgService.findByLoginUser(subject, null, true);

        List<Dict> treeList = new ArrayList<>();
        for (SysOrg sysOrg : list) {
            if (!sysOrg.getEnabled()) {
                continue;
            }
            Dict d = new Dict();
            d.set("title", sysOrg.getName());
            d.set("key", sysOrg.getId());
            String pid = sysOrg.getPid();
            d.set("parentKey", pid);
            d.set("iconName", getIconByType(sysOrg.getType()));
            treeList.add(d);
        }
        TreeManager<Dict> tm = TreeManager.of(treeList, "key", "parentKey");

        return AjaxResult.ok().data(tm.getTree());
    }

    private String getIconByType(int type) {
        switch (type) {
            case OrgType.UNIT -> {
                return "ApartmentOutlined";
            }
            case OrgType.DEPT -> {
                return "HomeOutlined";
            }

        }
        return "";
    }


    @PostMapping("sort")
    @HasPermission(label = "排序")
    public AjaxResult sort(@RequestBody DropEvent e) {
        sysOrgService.onDrop(e);
        return AjaxResult.ok().msg("排序成功");
    }
}
