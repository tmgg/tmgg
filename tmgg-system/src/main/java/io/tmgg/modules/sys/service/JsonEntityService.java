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
        log.info("database目录下的JSON实体初始化中...");
        // 解析
        List<JsonEntity> list = dao.findAll(true);
        log.info("实体个数:{}",list.size());

        // 保存
        long time = System.currentTimeMillis();
        List<String> ignoreList = ListUtil.toList( SysMenu.class.getSimpleName());
        for (JsonEntity info : list) {
            dao.saveToDatabase(info,ignoreList);
        }

        log.info("保存实体耗时:{}",System.currentTimeMillis() - time);

    }

    @Override
    public Collection<SysMenu> parseMenuList() throws Exception {
        List<JsonEntity> list = dao.findAll(true);

        List<SysMenu> sysMenuList = list.stream().filter(item -> item.getEntityName().equals("SysMenu")).map(item -> (SysMenu) item.getEntity()).toList();
        return sysMenuList;
    }







}

