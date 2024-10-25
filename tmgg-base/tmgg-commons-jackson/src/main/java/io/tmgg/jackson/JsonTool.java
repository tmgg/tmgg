package io.tmgg.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonTool {

    public static String toJson(Object o) throws JsonProcessingException {
        if(o == null){
            return null;
        }
        return getObjectMapper().writeValueAsString(o);
    }


    public static String toJsonQuietly(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return toJson(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // ignore
        }
        return null;
    }

    public static String toPrettyJsonQuietly(Object o) {
        if(o == null){
            return null;
        }
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // ignore
        }
        return null;
    }

    public static <T> T jsonToBean(String json, Class<T> cls)   throws IOException {
        if (json == null) {
            return null;

        }

        return getObjectMapper().readValue(json, cls);

    }


    public static <T> T jsonToBean(String json, TypeReference<T> valueTypeRef)
            throws IOException {
        if (json == null) {
            return null;

        }

        return getObjectMapper().readValue(json, valueTypeRef);
    }

    public static <T> T jsonToBeanQuietly(String json, Class<T> cls) {
        if(json == null){
            return null;
        }
        try {
            return jsonToBean(json, cls);

        } catch (Exception e) {
            e.printStackTrace(); // ignore
        }
        return null;
    }

    public static <T> List<T> jsonToBeanListQuietly(String json, Class<T> cls) {
        if (json == null) {
            return null;
        }
        try {
            ObjectMapper mapper = getObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, cls);
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace(); // ignore
        }
        return null;
    }


    public static <T> List<T> jsonToListQuietly(String json) {
        if (json == null) {
            return null;
        }
        try {
            ObjectMapper mapper = getObjectMapper();
            return mapper.readValue(json, List.class);
        } catch (Exception e) {
            e.printStackTrace(); // ignore
        }
        return null;
    }




    public static Object jsonToBeanQuietly(String json) {
        if (json == null) {
            return null;
        }
        return jsonToBeanQuietly(json, Object.class);
    }

    public static Map<String, Object> jsonToMapQuietly(String json) {
        if (json != null && !json.isEmpty()) {
            try {
                return getObjectMapper().readValue(json, new TypeReference<HashMap<String, Object>>() {
                });
            } catch (Exception e) {
                e.printStackTrace(); // ignore
            }
        }

        return new HashMap<>();
    }

    public static Map<String, Object> jsonToMap(String json)
            throws IOException {
        if (json != null && !json.isEmpty()) {
            return getObjectMapper().readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        }
        return new HashMap<>();
    }

    public static Map<String, String> jsonToMapStringStringQuietly(String json) {
        if (json == null) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, new TypeReference<HashMap<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // delay Initialize
    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }
        return objectMapper;
    }

    // singleton ,as to initialize need much TIME
    private static ObjectMapper objectMapper;

}
