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
        sysMenuService.deleteAll();

        sysMenuService.addDir(null,"biz","业务模块", null,0);
        sysMenuService.addDir(null,"sys","系统管理", null,100);

        sysMenuService.addDir("sys","sysMonitor","系统监控", AntDesignIcon.DesktopOutlined,199);





        sysMenuService.addDir("sys","jobMgr","作业调度", AntDesignIcon.ScheduleOutlined,7);
        sysMenuService.addMenu("jobMgr","job","作业管理", AntDesignIcon.OrderedListOutlined,"job","/job",1,false);
        sysMenuService.addMenu("jobMgr","jobLog","作业日志", AntDesignIcon.FileOutlined,"jobLog","/job/logList",2,false);
        sysMenuService.addMenu("jobMgr","jobStatus","作业监控", AntDesignIcon.FundViewOutlined,"jobStatus","/job/status",3,false);

        sysMenuService.addMenu("sys","sysManual","操作手册", AntDesignIcon.CopyOutlined,"sysManual","/system/sysManual",4,false);


        initParsers();


    }

    private void initParsers() throws Exception {
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
