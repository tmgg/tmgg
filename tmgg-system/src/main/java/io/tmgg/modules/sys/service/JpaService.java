package io.tmgg.modules.sys.service;

import io.tmgg.BasePackage;
import io.tmgg.SysProp;
import io.tmgg.lang.SpringTool;
import jakarta.annotation.Resource;
import jakarta.persistence.Entity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;

@Component
public class JpaService {

    @Resource
    SysProp sys;


    public <T> Class<T> findOne(String name) throws IOException, ClassNotFoundException {
        List<String> list = findAllNames();
        for (String clsName : list) {
            String simpleName = StringUtils.substringAfterLast(clsName, ".");
            if (simpleName.equalsIgnoreCase(name)) {
                return (Class<T>) Class.forName(clsName);
            }

        }
        return null;
    }

    public List<Class<?>> findAllClass() throws IOException, ClassNotFoundException {
        List<Class<?>> clsList = new ArrayList<>();
        List<String> allNames = findAllNames();
        for (String name : allNames) {
            Class<?> cls = Class.forName(name);
            clsList.add(cls);
        }
        return clsList;
    }

    public List<String> findAllNames() {
        Set<Class<?>> basePackageClasses = SpringTool.getBasePackageClasses();
        List<String> entityList = new LinkedList<>();
        for (Class<?> cls : basePackageClasses) {
            List<String> pkgEntityList = findBySuperClass(cls);
            entityList.addAll(pkgEntityList);
        }
        Collections.sort(entityList);

        return entityList;
    }


    private static List<String> findBySuperClass(Class baseClas) {
        try {

            String base = ClassUtils.convertClassNameToResourcePath(baseClas.getPackage().getName());
            String locationPattern = "classpath*:" + base + "/**/*.class";

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            org.springframework.core.io.Resource[] resources = resolver.getResources(locationPattern);


            MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resolver);

            List<String> list = new ArrayList<>();
            for (org.springframework.core.io.Resource resource : resources) {
                MetadataReader meta = readerfactory.getMetadataReader(resource);
                if (meta.getAnnotationMetadata().hasAnnotation(Entity.class.getName())) {

                    ClassMetadata classMetadata = meta.getClassMetadata();
                    list.add(classMetadata.getClassName());
                }
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
