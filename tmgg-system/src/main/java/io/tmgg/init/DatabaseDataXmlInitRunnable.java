package io.tmgg.init;

import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.JpaTool;
import io.tmgg.SystemProperties;
import io.tmgg.sys.menu.entity.SysMenu;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 初始化 database目录下的数据
 */
@Slf4j
@Component
public class DatabaseDataXmlInitRunnable implements Runnable {

    public static final String CLASSPATH_DATABASE_XML = "classpath*:database/*.xml";


    /**
     * 预留关键字: 查找主键
     **/
    public static final String ATTR_PK = "PK";

    /**
     * 预留关键字: 是否更新
     **/
    public static final String ATTR_UPDATE = "UPDATE";


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
                    String xml = IoUtil.readUtf8(is);
                    handleFile(xml);
                }
            }
        }catch (Exception e){
            throw  new IllegalStateException(e);
        }

    }

    private void handleFile(String xml) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        Elements els = doc.select("root").first().children();

        for (Element el : els) {
            handleRecord(el);
        }

    }

    private void handleRecord(Element el) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String entityName = el.tagName();

        Class entityCls = JpaTool.findEntityClass(entityName);
        if (entityCls == null) {
            System.out.println("实体标签异常 " + el.tagName());
            return;
        }

        if(!systemProperties.isMenuAutoUpdate() && (entityCls.equals(SysMenu.class)  )){
            log.warn("已关闭菜单更新，不再更新{}", entityCls);
            return;
        }


        String daoName = entityCls.getSimpleName() + "Dao";
        daoName = StrUtil.lowerFirst(daoName);
        BaseDao dao = SpringTool.getBean(daoName);

        Assert.state(dao != null, daoName + "不存在");

        Persistable<String> bean = (Persistable<String>) entityCls.getConstructor().newInstance();


        Map<String, Object> beanMap = this.parseBean(el);
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

        String updatable = (String) beanMap.get(ATTR_UPDATE);


        String findField = StringUtils.trimToNull((String) beanMap.get(ATTR_PK));


        Persistable old;
        if (findField == null) {
            old = dao.findOne(bean.getId());
        } else {
            Object findValue = beanMap.get(findField);
            old = dao.findOneByField(findField, findValue);
        }

        if (old == null || StrUtil.isEmpty(updatable) || "true".equals(updatable)) {
            dao.save(bean);
        }

    }

    private Map<String, Object> parseBean(Element el) {
        Map<String, Object> beanMap = new HashMap<>();

        // 设置element 属性为bean属性
        for (Attribute attr : el.attributes()) {
            beanMap.put(attr.getKey(), attr.getValue());
        }


        // 设置子节点为bean属性
        Elements fields = el.children();
        for (Element field : fields) {
            String key = field.tagName();
            String value = field.text();
            beanMap.put(key, value);

        }
        return beanMap;
    }
}
