package io.tmgg.init;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.common.AntDesignIcon;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SysMenuInit {

    @Resource
    private SysMenuService sysMenuService;



    public void init() throws Exception {
        Collection<SysMenuParser> parsers = SpringTool.getBeans(SysMenuParser.class);
        for (SysMenuParser parser : parsers) {
            Collection<SysMenu> menus = parser.parseMenuList();
            for (SysMenu menu : menus) {
                SysMenu old = sysMenuService.findOne(menu.getId());
                if(old ==null){
                    sysMenuService.save(menu);
                }else {
                    BeanUtil.copyProperties(menu,old);
                    sysMenuService.save(menu);
                }
            }
        }
    }




}
