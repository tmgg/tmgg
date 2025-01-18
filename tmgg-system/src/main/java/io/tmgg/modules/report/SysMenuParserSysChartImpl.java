package io.tmgg.modules.report;

import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.report.entity.SysReportChart;
import io.tmgg.modules.report.service.SysChartService;
import io.tmgg.modules.sys.entity.SysMenu;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SysMenuParserSysChartImpl implements SysMenuParser {

    @Resource
    SysChartService sysChartService;

    @Override
    public Collection<SysMenu> getMenuList() throws Exception {
        List<SysMenu> results = new ArrayList<>();
        List<SysReportChart> list = sysChartService.findAll();
        for (SysReportChart sysReportChart : list) {
            SysMenu sysMenu = sysChartService.buildMenu(sysReportChart);
            if(sysMenu != null){
                results.add(sysMenu);
            }
        }

        return results;
    }
}
