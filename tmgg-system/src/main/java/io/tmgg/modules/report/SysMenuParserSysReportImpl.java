package io.tmgg.modules.report;

import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.report.entity.SysReportChart;
import io.tmgg.modules.report.entity.SysReportTable;
import io.tmgg.modules.report.service.SysReportChartService;
import io.tmgg.modules.report.service.SysReportTableService;
import io.tmgg.modules.sys.entity.SysMenu;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class SysMenuParserSysReportImpl implements SysMenuParser {

    @Resource
    private SysReportChartService sysReportChartService;

    @Resource
    private SysReportTableService sysReportTableService;

    @Override
    public Collection<SysMenu> parseMenuList() throws Exception {
        List<SysMenu> results = new ArrayList<>();
        {
            List<SysReportChart> list = sysReportChartService.findAll();
            for (SysReportChart sysReportChart : list) {
                SysMenu sysMenu = sysReportChartService.buildMenu(sysReportChart);
                if (sysMenu != null) {
                    results.add(sysMenu);
                }
            }
        }

        {
            List<SysReportTable> list = sysReportTableService.findAll();
            for (SysReportTable sysReportChart : list) {
                SysMenu sysMenu = sysReportTableService.buildMenu(sysReportChart);
                if (sysMenu != null) {
                    results.add(sysMenu);
                }
            }
        }


        return results;
    }
}
