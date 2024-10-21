package io.tmgg.lang;

import io.tmgg.BasePackage;
import io.tmgg.SysProperties;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import jakarta.persistence.Entity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JpaTool {

    @Resource
    SysProperties sys;



    public  <T> Class<T> findEntityClass(String name) throws IOException, ClassNotFoundException {
        List<String> list = findAllEntity();
        for (String clsName : list) {
            String simpleName = StringUtils.substringAfterLast(clsName, ".");
            if (simpleName.equalsIgnoreCase(name)) {
                return (Class<T>) Class.forName(clsName);
            }

        }
        return null;
    }

    public  List<String> findAllEntity() throws IOException {
        List<String> entityList = findEntityList(BasePackage.class);

        if(!BasePackage.class.equals(sys.getBasePackageClass())){
            List<String> entityList2 = findEntityList(sys.getBasePackageClass());
            entityList.addAll(entityList2);
        }

        return entityList;
    }

    private static List<String> findEntityList(Class baseClas) throws IOException {
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
    }

}
