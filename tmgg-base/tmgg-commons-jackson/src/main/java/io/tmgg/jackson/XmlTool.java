package io.tmgg.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlTool {

    public static String beanToXml(Object bean) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();

        //忽略空属性
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ObjectWriter objectWriter = xmlMapper.writerWithDefaultPrettyPrinter();

        String xml = objectWriter.writeValueAsString(bean);
        return xml;
    }

    public static <T> T xmlToBean( String xml, Class<T> cls) throws JsonProcessingException {
        ObjectMapper objectMapper = new XmlMapper();
        return objectMapper.readValue(xml, cls);
    }
}
