package io.tmgg.modules.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.modules.SysMenuParser;
import io.tmgg.modules.sys.entity.SysMenu;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 初始化 database目录下的数据
 */
@Slf4j
@Component
public class JsonToDatabaseService implements SysMenuParser {

    public static final String CLASSPATH_DATABASE_XML = "classpath*:database/*.json";


    /**
     * 预留关键字: 查找主键, 如账号名
     **/
    private static final String ATTR_FIND_FIELD = "$find_field";

    /**
     * 预留关键字: 是否更新: true,false
     **/
    private static final String ATTR_UPDATE = "$update";

    @Resource
    JpaService jpaService;




    public void init() throws Exception {
        // 解析
        List<EntityInfo> list = this.parsJsonFile();


        // 保存
        for (EntityInfo info : list) {
            this.saveRecord(info);
        }

    }

    @Override
    public Collection<SysMenu> getMenuList() throws Exception {
        // 解析
        List<EntityInfo> list = this.parsJsonFile();

        List<SysMenu> sysMenuList = list.stream().filter(item -> item.entityName.equals("SysMenu")).map(item -> (SysMenu) item.entity).toList();
        return sysMenuList;
    }

    public List<EntityInfo> parsJsonFile() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources = resolver.getResources(CLASSPATH_DATABASE_XML);

        // 遍历文件内容
        List<EntityInfo> result = new ArrayList<>();

        for (org.springframework.core.io.Resource resource : resources) {
            log.info("解析文件 {}", resource.getFilename());
            try (InputStream is = resource.getInputStream()) {
                String json = IoUtil.readUtf8(is);
                Map<String, Object> map = JsonTool.jsonToMap(json);
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    String entityName = e.getKey();


                    List<Map<String, Object>> beanDataList = (List<Map<String, Object>>) e.getValue();
                    for (Map<String, Object> data : beanDataList) {
                        EntityInfo info = new EntityInfo(entityName, data);
                        this.convertToEntity(info);
                        result.add(info);
                    }
                }
            }
        }

        return result;
    }


    private void convertToEntity(EntityInfo info) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String entityName = info.entityName;
        Map<String, Object> mapData = info.mapData;

        Class entityCls = jpaService.findOne(entityName);
        if (entityCls == null) {
            System.err.println("实体标签异常 " + entityName);
            throw new IllegalStateException();
        }

        Persistable<String> entity = (Persistable<String>) entityCls.getConstructor().newInstance();

        for (Map.Entry<String, Object> e : mapData.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();

            // 忽略关键字
            if (StringUtils.containsAny(key, ATTR_FIND_FIELD, ATTR_UPDATE)) {
                continue;
            }

            BeanUtil.setFieldValue(entity, key, value);
        }


        Assert.state(!entity.isNew(), "实体数据必须包含ID：" + entity.getClass().getSimpleName());

        if (info.mapData.containsKey(ATTR_UPDATE)) {
            info.update = (Boolean) info.mapData.get(ATTR_UPDATE);
        }
        if (info.mapData.containsKey(ATTR_FIND_FIELD)) {
            info.findField = StringUtils.trimToNull((String) info.mapData.get(ATTR_FIND_FIELD));
        }
        info.findValue = info.mapData.get(info.findField);
        info.entity = entity;

    }

    private <T extends Persistable<String>> void saveRecord(EntityInfo info) throws IOException, ClassNotFoundException {
        String entityName = info.entityName;
        T entity = (T) info.entity;

        Assert.state(!entity.isNew(), "实体数据必须包含ID：" + entity.getClass().getSimpleName());


        Class<T> entityCls = jpaService.findOne(entityName);
        String daoName = entityCls.getSimpleName() + "Dao";
        daoName = StrUtil.lowerFirst(daoName);
        BaseDao<T> dao = SpringTool.getBean(daoName);

        Assert.notNull(dao, daoName + "不存在");
        T old = dao.findOneByField(info.findField, info.findValue);

        if (old == null || info.update) {
            dao.save(entity);
        }
    }

  public static class EntityInfo {
        String entityName;
        Map<String, Object> mapData;

        Persistable<String> entity;

        boolean update = true;
        String findField = "id";
        Object findValue = null;

        public EntityInfo(String entityName, Map<String, Object> mapData) {
            this.entityName = entityName;
            this.mapData = mapData;
        }
    }

}

