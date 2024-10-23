package io.tmgg.lang;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        return om().writeValueAsString(o);
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
            return om().writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // ignore
        }
        return null;
    }

    public static <T> T jsonToBean(String json, Class<T> cls)   throws IOException {
        if (json == null) {
            return null;

        }

        return om().readValue(json, cls);

    }


    public static <T> T jsonToBean(String json, TypeReference<T> valueTypeRef)
            throws IOException {
        if (json == null) {
            return null;

        }

        return om().readValue(json, valueTypeRef);
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
            ObjectMapper mapper = om();
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
            ObjectMapper mapper = om();
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
                return om().readValue(json, new TypeReference<HashMap<String, Object>>() {
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
            return om().readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        }
        return new HashMap<>();
    }

    public static Map<String, String> jsonToMapStringStringQuietly(String json) {
        if (json == null) {
            return null;
        }
        try {
            return om().readValue(json, new TypeReference<HashMap<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // delay Initialize
    private static ObjectMapper om() {
        if (om == null) {
            om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }
        return om;
    }

    // singleton ,as to initialize need much TIME
    private static ObjectMapper om;

}
