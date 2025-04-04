package io.tmgg.modules.report.service;

import cn.hutool.core.util.StrUtil;
import io.tmgg.dbtool.obj.ComplexResult;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.report.dao.SysReportTableDao;
import io.tmgg.modules.report.entity.SysReportTable;
import io.tmgg.modules.sys.dao.SysMenuDao;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.web.enums.MenuType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysReportTableService extends BaseService<SysReportTable> {


    @Resource
    private SysReportTableDao sysReportTableDao;

    @Resource
    private SysMenuDao sysMenuDao;

    @Override
    public SysReportTable saveOrUpdate(SysReportTable input) throws Exception {
        boolean isNew = input.isNew();
        if (isNew) {
            return baseDao.save(input);
        }

        String sysMenuPid = input.getSysMenuPid();

        SysReportTable chart = baseDao.findById(input.getId());
        chart.setSql(input.getSql());
        chart.setType(input.getType());
        chart.setSysMenuPid(sysMenuPid);
        chart.setTitle(input.getTitle());

        String menuId = chart.getId();
        sysMenuDao.deleteById(menuId);
        SysMenu sysMenu = buildMenu(chart);
        if (sysMenu != null) {
            sysMenuDao.save(sysMenu);
        }


        return baseDao.save(chart);
    }

    public SysMenu buildMenu(SysReportTable reportTable) {
        String code = reportTable.getCode();
        String sysMenuPid = reportTable.getSysMenuPid();
        if (StrUtil.isEmpty(sysMenuPid)) {
            return null;
        }
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(reportTable.getId());
        sysMenu.setName(reportTable.getTitle());
        sysMenu.setType(MenuType.MENU);
        sysMenu.setPid(sysMenuPid);

        sysMenu.setPerm(code);
        sysMenu.setVisible(true);
        sysMenu.setPerm(code);
        sysMenu.setPath("/report/table/" + code);

        return sysMenu;
    }




    public SysReportTable findByCode(String code) {
        return sysReportTableDao.findByCode( code);
    }

    @Transactional
    public void addViewCount(String code) {
        SysReportTable chart = findByCode(code);
        if (chart != null) {
            Integer viewCount = chart.getViewCount();
            viewCount = viewCount == null ? 0 : viewCount;
            chart.setViewCount(viewCount + 1);
        }
    }
}

