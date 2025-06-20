package io.tmgg.modules.system.dao;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.PersistEntity;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.system.entity.JsonEntity;
import io.tmgg.modules.system.service.JpaService;
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


    private Cache<String, List<JsonEntity>> cache = CacheUtil.newTimedCache(1000 * 60 * 5);

    @Resource
    private JpaService jpaService;


    public JsonEntity findOne(Class<?> entityCls, String id) throws Exception {
        List<JsonEntity> list = findAll(true);
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
        Assert.notNull(newFileContent, "替换json内容失败");
        FileUtil.writeUtf8String(newFileContent, file);

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


    public List<JsonEntity> findAll(boolean containsFramework) throws Exception {
        log.info("BEGIN 解析所有database目录下的json文件");
        long time = System.currentTimeMillis();
        String cacheKey = "findAll_" + containsFramework;
        if (cache.containsKey(cacheKey)) {
            log.info("END 命中缓存");
            return cache.get(cacheKey);
        }


        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<org.springframework.core.io.Resource> resources = List.of(resolver.getResources(CLASSPATH_DATABASE_XML));

        if (!containsFramework) {
            log.info("排除框架文件，即以framework开头的json文件");
            resources = resources.stream().filter(r -> !r.getFilename().startsWith("framework")).toList();
        }

        // 遍历文件内容
        List<JsonEntity> result = new ArrayList<>();
        for (org.springframework.core.io.Resource resource : resources) {

            List<JsonEntity> fileEntityList = parseFile(resource);
            result.addAll(fileEntityList);

        }

        cache.put(cacheKey, result);
        log.info("END 解析所有database目录下的json文件,耗时:{}", System.currentTimeMillis() - time);
        return result;
    }

    private List<JsonEntity> parseFile(org.springframework.core.io.Resource resource) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        long time = System.currentTimeMillis();
        String filename = resource.getFilename();
        log.info("解析文件 {}", filename);
        if (cache.containsKey(filename)) {
            log.info("命中缓存");
            return cache.get(filename);
        }

        List<JsonEntity> fileEntityList = new ArrayList<>();

        try (InputStream is = resource.getInputStream()) {
            String json = IoUtil.readUtf8(is);
            Map<String, Object> map = JsonTool.jsonToMap(json);
            for (Map.Entry<String, Object> e : map.entrySet()) {
                String entityName = e.getKey();


                List<Map<String, Object>> beanDataList = (List<Map<String, Object>>) e.getValue();
                for (Map<String, Object> data : beanDataList) {
                    JsonEntity info = new JsonEntity(entityName, data, resource.getURI());
                    this.convertToEntity(info);

                    fileEntityList.add(info);
                }
            }
        }
        cache.put(filename, fileEntityList);
        return fileEntityList;
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
        if (ignoreList.contains(entityName)) {
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
            dao.save(entity);
        }
    }
}
