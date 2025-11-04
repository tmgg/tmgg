
package io.tmgg.modules.system.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.framework.session.SysHttpSession;
import io.tmgg.lang.tree.TreeNodeDict;
import io.tmgg.lang.tree.TreeManager;
import io.tmgg.dto.AjaxResult;
import io.tmgg.lang.tree.drag.DragDropEvent;
import io.tmgg.lang.tree.drag.TreeDragTool;
import io.tmgg.modules.system.entity.OrgType;
import io.tmgg.modules.system.entity.SysOrg;
import io.tmgg.modules.system.service.SysOrgService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        if(sysOrg.getLeader() != null){
            if(StrUtil.isEmpty(sysOrg.getLeader().getId())){
                sysOrg.setLeader(null);
            }
        }
        sysOrgService.saveOrUpdate(sysOrg);
        session.removeAttribute(SysHttpSession.SUBJECT_KEY);
        return AjaxResult.ok().msg("保存机构成功");
    }

    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(@RequestBody SysOrg sysOrg, HttpSession session) {
        sysOrgService.deleteById(sysOrg.getId());
        session.removeAttribute(SysHttpSession.SUBJECT_KEY);
        return AjaxResult.ok().msg("删除机构成功");
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
        sysOrgService.toggleAllStatus(id, true);
        return AjaxResult.ok();
    }

    @HasPermission
    @GetMapping("disableAll")
    public AjaxResult disableAll(String id) {
        sysOrgService.toggleAllStatus(id, false);
        return AjaxResult.ok();
    }



    @Data
    public static class PageParam{
        boolean showDisabled;
        boolean showDept;
    }

    /**
     * 管理页面的树，包含禁用的
     *
     * @return
     */
    @HasPermission("sysOrg:page")
    @RequestMapping("pageTree")
    public AjaxResult pageTree(@RequestBody PageParam param, String searchText) {
        Subject subject = SecurityUtils.getSubject();

        List<SysOrg> list = sysOrgService.findByLoginUser(subject, param.showDept  , param.showDisabled);

        if(StrUtil.isNotEmpty(searchText)){
            list = list.stream().filter(t -> t.getName().contains(searchText)).collect(Collectors.toList());
        }



        return AjaxResult.ok().data(list2Tree(list));
    }


    private String getIconByType(int type) {
        switch (type) {
            case OrgType.UNIT -> {
                return "ApartmentOutlined";
            }
            case OrgType.DEPT -> {
                return "BorderOutlined";
            }

        }
        return "";
    }


    @PostMapping("sort")
    @HasPermission(label = "排序")
    public AjaxResult sort(@RequestBody DragDropEvent e) {
        List<SysOrg> nodes = sysOrgService.findAll();
        List<SysOrg> list = TreeDragTool.onDrop(e, nodes);
        for (int i = 0; i < list.size(); i++) {
            SysOrg sysOrg = list.get(i);
            sysOrg.setSeq(i);
        }

        return AjaxResult.ok().msg("排序成功");
    }


    @GetMapping("allTree")
    public AjaxResult allTree() throws Exception {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> list = this.sysOrgService.findByLoginUser(subject,true, true);

        return AjaxResult.ok().data(list2Tree(list));
    }



    @GetMapping("unitTree")
    public AjaxResult unitTree() throws Exception {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> list = this.sysOrgService.findByLoginUser(subject, false, false);

        list = list.stream().filter((o) -> !o.isDept()).collect(Collectors.toList());


        return AjaxResult.ok().data(list2Tree(list));
    }

    @GetMapping("deptTree")
    public AjaxResult deptTree() throws Exception {
        Subject subject = SecurityUtils.getSubject();
        List<SysOrg> list = this.sysOrgService.findByLoginUser(subject,true, false);

        return AjaxResult.ok().data(list2Tree(list));
    }


    public List<TreeNodeDict> list2Tree(List<SysOrg> list){
        List<TreeNodeDict> treeList = list.stream().map(o -> {
            String title = o.getName();
            if (!o.getEnabled()) {
                title = title + " [禁用]";
            }
            String pid = o.getPid();

            TreeNodeDict d = new TreeNodeDict();
            d.set("title", title);
            d.set("key", o.getId());
            d.set("parentKey", pid);
            d.set("iconName", getIconByType(o.getType()));

            // 兼容选择框
            d.set("value", o.getId());
            d.set("label", o.getName());

            // 兼容treeUtil工具
            d.set("id", o.getId());
            d.set("pid", pid);

            return d;
        }).collect(Collectors.toList());

        TreeManager<TreeNodeDict> tm = TreeManager.of(treeList);

        return tm.getTree();
    }

}
