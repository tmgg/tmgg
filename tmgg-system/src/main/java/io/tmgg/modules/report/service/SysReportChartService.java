package io.tmgg.modules.report.service;

import cn.hutool.core.util.StrUtil;
import io.tmgg.dbtool.obj.ComplexResult;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.report.dao.SysReportChartDao;
import io.tmgg.modules.report.entity.SysReportChart;
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
public class SysReportChartService extends BaseService<SysReportChart> {


    @Resource
    private SysReportChartDao sysReportChartDao;

    @Resource
    private SysMenuDao sysMenuDao;

    @Override
    public SysReportChart saveOrUpdate(SysReportChart input) throws Exception {
        boolean isNew = input.isNew();
        if (isNew) {
            return baseDao.save(input);
        }

        String sysMenuPid = input.getSysMenuPid();

        SysReportChart chart = baseDao.findById(input.getId());
        chart.setSql(input.getSql());
        chart.setType(input.getType());
        chart.setSysMenuPid(sysMenuPid);
        chart.setTitle(input.getTitle());
        chart.setSysMenuSeq(input.getSysMenuSeq());

        String menuId = chart.getId();
        sysMenuDao.deleteById(menuId);
        SysMenu sysMenu = buildMenu(chart);
        if (sysMenu != null) {
            sysMenuDao.save(sysMenu);
        }


        return baseDao.save(chart);
    }

    public SysMenu buildMenu(SysReportChart chart) {
        String code = chart.getCode();
        String sysMenuPid = chart.getSysMenuPid();
        if (StrUtil.isEmpty(sysMenuPid)) {
            return null;
        }

        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(chart.getId());
        sysMenu.setName(chart.getTitle());
        sysMenu.setType(MenuType.MENU);
        sysMenu.setPid(sysMenuPid);

        sysMenu.setPerm(code);
        sysMenu.setVisible(true);
        sysMenu.setPerm(code);
        sysMenu.setPath("/report/chart/" + code);

        return sysMenu;
    }


    public Map<String, Object> buildEchartsOption(String title, String type, ComplexResult data) {
        Map<String, Object> option = new HashMap<>();

        if (StrUtil.isNotEmpty(title)) {
            option.put("title", Map.of("text", title));
        }
        option.put("legend", Map.of());


        String[] keys = data.getKeys();

        switch (type) {
            case "pie": {
                ArrayList<Object> series = new ArrayList<>(1);
                option.put("series", series);


                Map<String, Object> item = new HashMap<>();
                item.put("type", type);
                String nameKey = keys[0];
                String valueKey = keys[1];

                List<Map<String, Object>> pieDataList = data.getDataList().stream().map(i -> Map.of("name", i.get(nameKey), "value", i.get(valueKey))).collect(Collectors.toList());
                item.put("data", pieDataList);
                series.add(item);
                break;
            }
            case "line":
            case "bar":
                option.put("xAxis", Map.of("data", data.getKeyedMapList().get(keys[0])));
                option.put("yAxis", Map.of());
                ArrayList<Object> series = new ArrayList<>(keys.length - 1);
                option.put("series", series);

                for (int i = 1; i < keys.length; i++) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", type);
                    item.put("data", data.getKeyedMapList().get(keys[i]));
                    item.put("name", keys[i]);
                    series.add(item);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }


        return option;
    }

    public SysReportChart findByCode(String code) {
        return sysReportChartDao.findByCode( code);
    }

    @Transactional
    public void addViewCount(String code) {
        SysReportChart chart = findByCode(code);
        if (chart != null) {
            Integer viewCount = chart.getViewCount();
            viewCount = viewCount == null ? 0 : viewCount;
            chart.setViewCount(viewCount + 1);
        }
    }
}

