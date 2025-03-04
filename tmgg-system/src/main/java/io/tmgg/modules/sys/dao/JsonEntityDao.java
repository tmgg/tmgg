package io.tmgg.modules.sys.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.modules.sys.entity.JsonEntity;
import io.tmgg.modules.sys.service.JpaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JsonEntityDao {
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
    private JpaService jpaService;


    public JsonEntity findOne(Class<?> entityCls, String id) throws Exception {
        List<JsonEntity> list = findAll();
        for (JsonEntity entity : list) {
            if(entity.getEntity().getClass().equals(entityCls) && id.equals(entity.getEntity().getId())){
                return entity;
            }
        }

        return null;
    }

    public void save(JsonEntity entity){
        URI uri = entity.getUri();
        log.info("修改的json文件为：{}", uri);

        Assert.state(uri.getScheme().equals("file"), "该菜单json文件非文件系统中:" + uri.getScheme());

        String path = uri.getPath();
        path = path.replace("target/classes", "src/main/resources");

        File file = new File(path);
        Assert.state(file.exists(), "文件不存在:" + path);

        Map<String, Object> data = entity.getData();



    }

    public <T extends Persistable<String>> void saveToDatabase(JsonEntity info) throws IOException, ClassNotFoundException {
        String entityName = info.getEntityName();
        T entity = (T) info.getEntity();

        Assert.state(!entity.isNew(), "实体数据必须包含ID：" + entity.getClass().getSimpleName());


        Class<T> entityCls = jpaService.findOne(entityName);
        String daoName = entityCls.getSimpleName() + "Dao";
        daoName = StrUtil.lowerFirst(daoName);
        BaseDao<T> dao = SpringTool.getBean(daoName);

        Assert.notNull(dao, daoName + "不存在");
        T old = dao.findOneByField(info.getFindField(), info.getFindValue());

        if (old == null || info.isUpdate()) {
            dao.save(entity);
        }
    }
    public List<JsonEntity> findAll() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources = resolver.getResources(CLASSPATH_DATABASE_XML);

        // 遍历文件内容
        List<JsonEntity> result = new ArrayList<>();

        for (org.springframework.core.io.Resource resource : resources) {
            log.info("解析文件 {}", resource.getFilename());
            try (InputStream is = resource.getInputStream()) {
                String json = IoUtil.readUtf8(is);
                Map<String, Object> map = JsonTool.jsonToMap(json);
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    String entityName = e.getKey();


                    List<Map<String, Object>> beanDataList = (List<Map<String, Object>>) e.getValue();
                    for (Map<String, Object> data : beanDataList) {
                        JsonEntity info = new JsonEntity(entityName, data, resource.getURI());
                        this.convertToEntity(info);
                        result.add(info);
                    }
                }
            }
        }

        return result;
    }


    private void convertToEntity(JsonEntity info) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String entityName = info.getEntityName();
        Map<String, Object> mapData = info.getData();

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

        if (info.getData().containsKey(ATTR_UPDATE)) {
            info.setUpdate((Boolean) info.getData().get(ATTR_UPDATE));
        }
        if (info.getData().containsKey(ATTR_FIND_FIELD)) {
            info.setFindField(StringUtils.trimToNull((String) info.getData().get(ATTR_FIND_FIELD)));
        }
        info.setFindValue(info.getData().get(info.getFindField()));
        info.setEntity(entity);

    }
}
