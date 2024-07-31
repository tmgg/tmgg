package io.tmgg.lang.dao;

import io.tmgg.BasePackage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JpaTool {




    public static <T> Class<T> findEntityClass(String name) throws IOException, ClassNotFoundException {
        List<String> list = findAllEntity();
        for (String clsName : list) {
            String simpleName = StringUtils.substringAfterLast(clsName, ".");
            if (simpleName.equalsIgnoreCase(name)) {
                return (Class<T>) Class.forName(clsName);
            }

        }
        return null;
    }

    public static List<String> findAllEntity() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        String base = ClassUtils.convertClassNameToResourcePath(BasePackage.BASE_PACKAGE);

        String locationPattern = "classpath*:" + base + "/**/*.class";
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
