package io.tmgg.init;

import io.tmgg.lang.JsonTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.JpaTool;
import io.tmgg.SystemProperties;
import io.tmgg.sys.app.entity.SysApp;
import io.tmgg.sys.menu.entity.SysMenu;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
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
public class DatabaseDataJsonInitRunnable implements Runnable {

    public static final String CLASSPATH_DATABASE_XML = "classpath*:database/*.json";



    /**
     * 预留关键字: 查找主键, 如账号名
     **/
    private static final String ATTR_PK = "$pk";

    /**
     * 预留关键字: 是否更新: true,false
     **/
    private static final String ATTR_UPDATE = "$update";


    @Resource
    SystemProperties systemProperties;


    @Override
    public void run() {
        try {
            log.info("开始解析并初始化默认数据");
            log.info("开始初始化默认数据，扫描路径为 {}" , CLASSPATH_DATABASE_XML);

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            org.springframework.core.io.Resource[] resources = resolver.getResources(CLASSPATH_DATABASE_XML);

            // 遍历文件内容
            for (org.springframework.core.io.Resource resource : resources) {
                log.info("解析文件 {}", resource.getFilename());
                try (InputStream is = resource.getInputStream()) {
                    String json = IoUtil.readUtf8(is);
                    Map<String, Object> map = JsonTool.jsonToMapQuietly(json);

                    for (Map.Entry<String, Object> e : map.entrySet()) {
                        String className = e.getKey();
                        List<Map<String, Object>> beanDataList = (List<Map<String, Object>>) e.getValue();
                        for (Map<String, Object> beanData : beanDataList) {
                            handleRecord(className, beanData);
                        }
                    }
                }
            }
        }catch (Exception e){
            throw  new IllegalStateException(e);
        }

    }



    private void handleRecord(String entityName, Map<String, Object> beanMap) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class entityCls = JpaTool.findEntityClass(entityName);
        if (entityCls == null) {
            System.err.println("实体标签异常 " + entityName);
            return;
        }

        if(!systemProperties.isMenuAutoUpdate() && (entityCls.equals(SysMenu.class) || entityCls.equals(SysApp.class) )){
            log.warn("已关闭菜单更新，不再更新{}", entityCls);
            return;
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
