package io.tmgg.modules.sys.service;

import cn.hutool.core.collection.ListUtil;
import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.sys.dao.JsonEntityFileDao;
import io.tmgg.modules.sys.entity.JsonEntity;
import io.tmgg.modules.sys.entity.SysMenu;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 初始化 database目录下的数据
 */
@Slf4j
@Component
public class JsonEntityService implements SysMenuParser {

    @Resource
    private JsonEntityFileDao dao;



    public void initOnStartup() throws Exception {
        // 解析
        List<JsonEntity> list = dao.findAll();

        // 保存
        List<String> ignoreList = ListUtil.toList( SysMenu.class.getSimpleName());
        for (JsonEntity info : list) {
            dao.saveToDatabase(info,ignoreList);
        }

    }

    @Override
    public Collection<SysMenu> parseMenuList() throws Exception {
        List<JsonEntity> list = dao.findAll();

        List<SysMenu> sysMenuList = list.stream().filter(item -> item.getEntityName().equals("SysMenu")).map(item -> (SysMenu) item.getEntity()).toList();
        return sysMenuList;
    }







}

