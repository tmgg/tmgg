package io.tmgg.lang.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class JsonTest {

    public static final String JSON = """
            {"name":"张三"}
            """;

    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        UserNode userNode = mapper.readValue(JSON, UserNode.class);
        JsonNode tree = mapper.readTree(JSON);
        System.out.println(userNode);


        tree.fieldNames().forEachRemaining(System.out::println);


    }
}
