package io.tmgg.modules.sys.controller;

import io.tmgg.lang.dao.BaseController;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.entity.SysMenuBadge;
import io.tmgg.modules.sys.service.SysMenuBadgeService;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sysMenuBadge")
public class SysMenuBadgeController  extends BaseController<SysMenuBadge>{

    @Resource
    SysMenuBadgeService service;

    @Resource
    SysMenuService sysMenuService;


    // 查询参数，如果比较简单，可以直接用实体代替
    @Data
    public static class QueryParam {
        private  String keyword; // 仅有一个搜索框时的搜索文本


            private java.lang.String menuId;

            private java.lang.String url;

    }

    private  JpaQuery<SysMenuBadge> buildQuery(QueryParam param) {
        JpaQuery<SysMenuBadge> q = new JpaQuery<>();
      /*  DateRange dateRange = param.getDateRange();
        if(dateRange != null && dateRange.isNotEmpty()){
            q.between(PointsItemRecord.Fields.redeemTime, dateRange.getBegin(), dateRange.getEnd());
        }*/
        return q;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody  QueryParam param,  @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<SysMenuBadge> q = buildQuery(param);

        Map<String, SysMenu> menuMap = sysMenuService.findMenuMap();

        Page<SysMenuBadge> page = service.findAll(q, pageable);

        for (SysMenuBadge m : page) {
            String menuId = m.getMenuId();
            SysMenu sysMenu = menuMap.get(menuId);
            if(sysMenu !=null){
                m.setMenuName(sysMenu.getName());
            }
        }

        return AjaxResult.ok().data(page);
    }



    @GetMapping("menuOptions")
    public AjaxResult menuOptions() throws Exception {
        JpaQuery<SysMenu> q = new JpaQuery<>();
        List<SysMenu> menus = sysMenuService.findMenuVisible();

        List<TreeOption> treeOptions = TreeOption.convertTree(menus, BaseEntity::getId, SysMenu::getPid, SysMenu::getName);

        return AjaxResult.ok().data(treeOptions);
    }
}

