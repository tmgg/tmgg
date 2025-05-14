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
 * 初始化数据
 *
 * 初始化的数据可以放到resources/database目录,系统启动时回自动解析保存入库
 *
 * 数据格式为json, key为实体名称，value为数据数组。每个字段都对应实体，如果字段是枚举，填写枚举值即可
 * 例如
 * ```json
 *
 * {
 *   "SysMenu": [
 *     {
 *       "application": "system",
 *       "id": "sysOrg",
 *       "name": "机构管理",
 *       "code": "sysOrg",
 *       "router": "/system/org",
 *       "type": "MENU",
 *       "status": "ENABLE",
 *       "visible": "Y",
 *       "icon": "ApartmentOutlined",
 *       "seq": "1"
 *     }
 *   ]
 * }
 *
 * ```
 * 特殊字段
 * - $update true|false   控制数据是否更新
 * - $pk     String       默认是通过id来判断是否存在，以便判断是新增还是更新操作，如果想通过其他字段判断唯一性，如 $pk:"code"
 *
 *
 * @gendoc
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

