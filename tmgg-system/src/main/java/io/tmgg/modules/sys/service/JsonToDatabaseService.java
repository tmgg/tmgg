package io.tmgg.modules.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import io.tmgg.SysProp;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.web.db.DbCacheDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 初始化 database目录下的数据
 */
@Slf4j
@Component
public class JsonToDatabaseService {

    public static final String CLASSPATH_DATABASE_XML = "classpath*:database/*.json";

    public static final String CACHE_PREFIX = "DATABASE_JSON_FILE_";

    /**
     * 预留关键字: 查找主键, 如账号名
     **/
    private static final String ATTR_PK = "$pk";

    /**
     * 预留关键字: 是否更新: true,false
     **/
    private static final String ATTR_UPDATE = "_update";


    @Resource
    SysProp sysProp;

    @Resource
    JpaService jpaService;

    @Resource
    DbCacheDao dbCacheDao;


    /**
     *
     */
    public void parseAndSave(Class<?> cls) {
        try {
            log.info("开始解析并初始化默认数据");
            log.info("开始初始化默认数据，扫描路径为 {}", CLASSPATH_DATABASE_XML);

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            org.springframework.core.io.Resource[] resources = resolver.getResources(CLASSPATH_DATABASE_XML);

            // 遍历文件内容

            Map<String, String> oldMd5Dict = dbCacheDao.findDictByPrefix(CACHE_PREFIX);
            for (org.springframework.core.io.Resource resource : resources) {
                log.info("解析文件 {}", resource.getFilename());
                try (InputStream is = resource.getInputStream()) {
                    String json = IoUtil.readUtf8(is);

                    String cacheKey = CACHE_PREFIX + resource.getFilename();
                    String md5 = MD5.create().digestHex(json);
                    String oldMd5 = oldMd5Dict.get(cacheKey);
                    if (!sysProp.isForeUpdateJsonDatabase()) {
                        if (md5.equals(oldMd5)) {
                            log.info("文件内容md5相同，忽略 {}", resource.getFilename());
                            continue;
                        }
                    }
                    Map<String, Object> map = JsonTool.jsonToMap(json);

                    for (Map.Entry<String, Object> e : map.entrySet()) {
                        String entityName = e.getKey();
                        List<Map<String, Object>> beanDataList = (List<Map<String, Object>>) e.getValue();
                        for (Map<String, Object> beanData : beanDataList) {
                            if(cls == null || cls.getSimpleName().equals(entityName)){
                                handleRecord(entityName, beanData);
                            }
                        }
                    }
                    dbCacheDao.save(cacheKey, md5);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public void  cleanCache(){
        dbCacheDao.cleanByPrefix(CACHE_PREFIX);
    }

    private void handleRecord(String entityName, Map<String, Object> beanMap) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class entityCls = jpaService.findOne(entityName);
        if (entityCls == null) {
            System.err.println("实体标签异常 " + entityName);
            throw new IllegalStateException();
        }

        String daoName = entityCls.getSimpleName() + "Dao";
        daoName = StrUtil.lowerFirst(daoName);
        BaseDao dao = SpringTool.getBean(daoName);

        Assert.state(dao != null, daoName + "不存在");

        Persistable<String> bean = (Persistable<String>) entityCls.getConstructor().newInstance();


        for (Map.Entry<String, Object> e : beanMap.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();

            // 忽略关键字
            if (StringUtils.containsAny(key, ATTR_PK, ATTR_UPDATE)) {
                continue;
            }

            BeanUtil.setFieldValue(bean, key, value);
        }


        Assert.state(!bean.isNew(), "实体数据必须包含ID：" + bean.getClass().getSimpleName());

        Boolean updatable = (Boolean) beanMap.get(ATTR_UPDATE);


        String findField = StringUtils.trimToNull((String) beanMap.get(ATTR_PK));


        Persistable old;
        if (findField == null) {
            old = dao.findOne(bean.getId());
        } else {
            Object findValue = beanMap.get(findField);
            old = dao.findOneByField(findField, findValue);
        }

        if (old == null || updatable == null || updatable) {
            dao.save(bean);
        }

    }


}
