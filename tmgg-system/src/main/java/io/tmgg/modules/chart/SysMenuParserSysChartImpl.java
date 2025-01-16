package io.tmgg.modules.chart;

import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.chart.entity.SysChart;
import io.tmgg.modules.chart.service.SysChartService;
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
        List<SysChart> list = sysChartService.findAll();
        for (SysChart sysChart : list) {
            SysMenu sysMenu = sysChartService.buildMenu(sysChart);
            if(sysMenu != null){
                results.add(sysMenu);
            }
        }

        return results;
    }
}
