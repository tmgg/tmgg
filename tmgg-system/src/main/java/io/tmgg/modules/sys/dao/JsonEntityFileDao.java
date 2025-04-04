package io.tmgg.modules.sys.dao;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.PersistEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.sys.entity.JsonEntity;
import io.tmgg.modules.sys.service.JpaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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
public class JsonEntityFileDao {
    public static final String CLASSPATH_DATABASE_XML = "classpath*:database/*.json";


    /**
     * 预留关键字: 查找主键, 如账号名
     **/
    private static final String ATTR_FIND_FIELD = "$find_field";

    /**
     * 预留关键字: 是否更新: true,false
     **/
    private static final String ATTR_UPDATE = "$update";


    private Cache<String,List<JsonEntity> > cache = CacheUtil.newTimedCache(1000 * 60 * 5);

    @Resource
    private JpaService jpaService;


    public JsonEntity findOne(Class<?> entityCls, String id) throws Exception {
        List<JsonEntity> list = findAll();
        for (JsonEntity entity : list) {
            if (entity.getEntity().getClass().equals(entityCls) && id.equals(entity.getEntity().getId())) {
                return entity;
            }
        }

        return null;
    }

    public void save(JsonEntity jsonEntity) throws Exception {
        String id = (String) jsonEntity.getData().get("id");
        Assert.hasText(id, "id不能为空");
        URI uri = jsonEntity.getUri();
        log.info("修改的json文件为：{}", uri);

        Assert.state(uri.getScheme().equals("file"), "该菜单json文件非文件系统中:" + uri.getScheme());

        String path = uri.getPath();
        path = path.replace("target/classes", "src/main/resources");

        File file = new File(path);
        Assert.state(file.exists(), "文件不存在:" + path);

        String newFileContent = getNewFileContent(jsonEntity, file, id);
        Assert.notNull(newFileContent,"替换json内容失败");
        FileUtil.writeUtf8String(newFileContent,file);

        cache.clear();
    }

    private static String getNewFileContent(JsonEntity jsonEntity, File file, String id) throws IOException {
        try (InputStream is = FileUtil.getInputStream(file)) {
            String json = IoUtil.readUtf8(is);
            Map<String, Object> fileData = JsonTool.jsonToMap(json);
            for (Map.Entry<String, Object> e : fileData.entrySet()) {
                String entityName = e.getKey();
                List<Map<String, Object>> beanDataList = (List<Map<String, Object>>) e.getValue();
                for (int i = 0; i < beanDataList.size(); i++) {
                    Map<String, Object> data = beanDataList.get(i);
                    if (entityName.equals(jsonEntity.getEntityName()) && id.equals(data.get("id"))) {
                        beanDataList.set(i, jsonEntity.getData());

                        return JsonTool.toPrettyJsonQuietly(fileData);
                    }
                }
            }


        }
        return null;
    }


    public List<JsonEntity> findAll() throws Exception {
        List<JsonEntity> cacheList = cache.get("findAll");
        if(cacheList != null){
            return cacheList;
        }


        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources = resolver.getResources(CLASSPATH_DATABASE_XML);

        // 遍历文件内容
        List<JsonEntity> result = new ArrayList<>();

        log.info("BEGIN 解析所有database目录下的json文件");
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
        log.info("END 解析所有database目录下的json文件");

        cache.put("findAll", result);

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

        PersistEntity entity = (PersistEntity) entityCls.getConstructor().newInstance();

        for (Map.Entry<String, Object> e : mapData.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();

            // 忽略关键字
            if (StringUtils.containsAny(key, ATTR_FIND_FIELD, ATTR_UPDATE)) {
                continue;
            }

            BeanUtil.setFieldValue(entity, key, value);
        }


        Assert.state(entity.getId() != null, "实体数据必须包含ID：" + entity.getClass().getSimpleName());

        if (info.getData().containsKey(ATTR_UPDATE)) {
            info.setUpdate((Boolean) info.getData().get(ATTR_UPDATE));
        }
        if (info.getData().containsKey(ATTR_FIND_FIELD)) {
            info.setFindField(StringUtils.trimToNull((String) info.getData().get(ATTR_FIND_FIELD)));
        }
        info.setFindValue(info.getData().get(info.getFindField()));
        info.setEntity(entity);

    }

    public <T extends PersistEntity> void saveToDatabase(JsonEntity info, List<String> ignoreList) throws IOException, ClassNotFoundException {
        String entityName = info.getEntityName();
        if(ignoreList.contains(entityName)){
            return;
        }
        T entity = (T) info.getEntity();

        Assert.state(entity.getId() != null, "实体数据必须包含ID：" + entity.getClass().getSimpleName());


        Class<T> entityCls = jpaService.findOne(entityName);
        String daoName = entityCls.getSimpleName() + "Dao";
        daoName = StrUtil.lowerFirst(daoName);
        BaseDao<T> dao = SpringTool.getBean(daoName);

        Assert.notNull(dao, daoName + "不存在");
        JpaQuery<T> q = new JpaQuery<>();
        q.eq(info.getFindField(), info.getFindValue());
        T old = dao.findOne(q);

        if (old == null || info.isUpdate()) {
            dao.replace(entity);
        }
    }
}
