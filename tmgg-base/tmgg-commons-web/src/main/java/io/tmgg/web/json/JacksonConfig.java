package io.tmgg.web.json;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.tmgg.web.json.ignore.JsonIgnoreIntrospector;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * jackson 的配置
 * 自定义一些关于时间的 格式
 *
 * @author jiangtao
 */
@Configuration
public class JacksonConfig {


    static class LongToStringSerializer extends JsonSerializer<Long> {

        @Override
        public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(value.toString());
        }
    }

    /**
     * json自定义序列化工具,long转string
     *
     *
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {

            // 注册 Java 8 时间模块
            builder.modules(new JavaTimeModule());

            builder.annotationIntrospector(new JsonIgnoreIntrospector());

            LongToStringSerializer longToStringSerializer = new LongToStringSerializer();
            builder
                    .serializerByType(Long.class, longToStringSerializer)
                    .serializerByType(Long.TYPE, longToStringSerializer)

                    // 日期 -> 字符串
                    .serializerByType(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")))
                    .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))


                    // 字符串 -> 日期
                    .deserializerByType(Date.class, new DateJacksonConverter())
                    .deserializerByType(LocalDate.class, new LocalDateJacksonConverter())
                    .deserializerByType(LocalTime.class, new LocalTimeJacksonConverter())
                    .deserializerByType(LocalDateTime.class, new LocalDateTimeJacksonConverter())

            ;
        };
    }


}


/**
 * 自定义Jackson反序列化日期类型时应用的类型转换器, 可接受多种前端出入得格式
 *
 * @author jiangtao
 */
class DateJacksonConverter extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String originDate = p.getText();

        return DateUtil.parse(originDate);
    }

    @Override
    public Class<?> handledType() {
        return Date.class;
    }
}

/**
 * 自定义Jackson反序列化日期类型时应用的类型转换器, 可接受多种前端出入得格式
 *
 * @author jiangtao
 */
class LocalDateJacksonConverter extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String originDate = p.getText();
        DateTime dateTime = DateUtil.parse(originDate);

        LocalDateTime localTime = LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault());

        return localTime.toLocalDate();
    }


    @Override
    public Class<?> handledType() {
        return LocalDate.class;
    }
}

/**
 * 自定义Jackson反序列化日期类型时应用的类型转换器, 可接受多种前端出入得格式
 *
 * @author jiangtao
 */
class LocalTimeJacksonConverter extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String originDate = p.getText();
        DateTime dateTime = DateUtil.parse(originDate);
        LocalDateTime localTime = LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault());


        return localTime.toLocalTime();
    }


    @Override
    public Class<?> handledType() {
        return LocalTime.class;
    }
}


class LocalDateTimeJacksonConverter extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String originDate = p.getText();
        DateTime dateTime = DateUtil.parse(originDate);


        return LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault());
    }


    @Override
    public Class<?> handledType() {
        return LocalDateTime.class;
    }
}
