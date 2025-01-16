package io.tmgg.modules.chart.service;

import cn.hutool.core.util.StrUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.chart.QueryData;
import io.tmgg.modules.chart.dao.SysChartDao;
import io.tmgg.modules.chart.entity.SysChart;
import io.tmgg.modules.sys.dao.SysMenuDao;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.web.enums.MenuType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysChartService extends BaseService<SysChart> {

    @Resource
    DbTool db;

    @Resource
    SysChartDao sysChartDao;

    @Resource
    SysMenuDao sysMenuDao;

    @Override
    public SysChart saveOrUpdate(SysChart input) throws Exception {
        boolean isNew = input.isNew();
        if (isNew) {
            return baseDao.save(input);
        }

        String sysMenuPid = input.getSysMenuPid();

        SysChart chart = baseDao.findOne(input);
        chart.setSql(input.getSql());
        chart.setType(input.getType());
        chart.setSysMenuPid(sysMenuPid);

        String code = chart.getCode();
        String menuId = "sysChart-" + code;
        sysMenuDao.deleteById(menuId);
        SysMenu sysMenu = buildMenu(chart);
        if (sysMenu != null) {
            sysMenuDao.save(sysMenu);
        }


        return baseDao.save(chart);
    }

    public SysMenu buildMenu(SysChart chart) {
        String code = chart.getCode();
        String sysMenuPid = chart.getSysMenuPid();
        if (StrUtil.isEmpty(sysMenuPid)) {
            return null;
        }
        String menuId = "sysChart-" + code;

        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(menuId);
        sysMenu.setName(chart.getTitle());
        sysMenu.setType(MenuType.MENU);
        sysMenu.setPid(sysMenuPid);

        sysMenu.setPerm(code);
        sysMenu.setVisible(true);
        sysMenu.setPerm(code);
        sysMenu.setPath("/chart/sysChart/" + code);

        return sysMenu;
    }

    public QueryData runSql(String sql) {
        QueryData result = new QueryData();

        List<Map<String, Object>> list = db.findAll(sql);

        String[] keys = getSqlKeys(sql);
        result.setKeys(keys);

        for (String key : keys) {
            result.getData().put(key, new Object[list.size()]);
        }

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> row = list.get(i);
            for (String key : keys) {
                Object v = row.get(key);
                Object[] rowData = result.getData().get(key);
                rowData[i] = v;
            }
        }
        result.setListData(list);

        return result;
    }

    public String[] getSqlKeys(String sql) {
        String[] list = db.getKeys(sql);
        return list;
    }


    public Map<String, Object> buildEchartsOption(String title, String type, QueryData data) {
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

                List<Map<String, Object>> pieDataList = data.getListData().stream().map(i -> Map.of("name", i.get(nameKey), "value", i.get(valueKey))).collect(Collectors.toList());
                item.put("data", pieDataList);
                series.add(item);
                break;
            }
            case "line":
            case "bar":
                option.put("xAxis", Map.of("data", data.getData().get(keys[0])));
                option.put("yAxis", Map.of());
                ArrayList<Object> series = new ArrayList<>(keys.length - 1);
                option.put("series", series);

                for (int i = 1; i < keys.length; i++) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", type);
                    item.put("data", data.getData().get(keys[i]));
                    item.put("name", keys[i]);
                    series.add(item);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }


        return option;
    }

    public SysChart findByCode(String code) {
        return sysChartDao.findOneByField("code", code);
    }
}

