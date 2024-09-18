package io.tmgg.lang;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceTool {

    /**
     * @param path , 支持通配符 如database/*.xml
     *
     * @return  资源
     *
     * @throws IOException IO异常
     */
    public static Resource[] findAll(String path) throws IOException {
        String classPath = "classpath*:" + path;

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources = resolver.getResources(classPath);

        return resources;
    }


    public static Resource findOne(String path) throws IOException {
        return new ClassPathResource(path);
    }

    public static String readString(String path) throws IOException {
        Resource r = findOne(path);
        InputStream is = r.getInputStream();

        String body = IOUtils.toString(is, StandardCharsets.UTF_8);

        IOUtils.closeQuietly(is);

        return body;
    }
}
