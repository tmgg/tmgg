
package io.tmgg.sys.perm;

import io.tmgg.lang.obj.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 系统菜单
 */
@RestController
@RequestMapping("sysMenu")
@Slf4j
public class SysMenuController  {

    @Resource
    private SysMenuService sysMenuService;



    @GetMapping("treeForGrant")
    public AjaxResult treeForGrant() {
        List<MenuTreeNode> data = sysMenuService.treeForGrant();
        return AjaxResult.ok().data(data);
    }



}
