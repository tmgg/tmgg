package io.tmgg.dbtool;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.Converter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.lang.TypeReference;
import io.tmgg.lang.ConvertTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConvertTest {

    @Test
    public void localDateTime(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        LocalDateTime a = ts.toLocalDateTime();
        LocalDateTime b = ConvertTool.convert(LocalDateTime.class, ts);
        Assertions.assertEquals(a,b);
    }

    @Test
    public void localDate(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        LocalDate a = ts.toLocalDateTime().toLocalDate();
        LocalDate b = ConvertTool.convert(LocalDate.class, ts);
        Assertions.assertEquals(a,b);
    }


    @Test
    public void int2Enum(){
        TimeUnit[] values = TimeUnit.values();
        for (int i = 0; i < values.length; i++) {
            TimeUnit a = values[i];
            TimeUnit b = ConvertTool.convert(TimeUnit.class, i);
            Assertions.assertEquals(a, b);
        }
    }

    @Test
    public void long2Enum(){
        TimeUnit[] values = TimeUnit.values();
        for (int i = 0; i < values.length; i++) {
            TimeUnit a = values[i];
            TimeUnit b = ConvertTool.convert(TimeUnit.class, Long.valueOf(i));
            Assertions.assertEquals(a, b);
        }
    }
    @Test
    public void str2Enum(){
        TimeUnit[] values = TimeUnit.values();
        for (int i = 0; i < values.length; i++) {
            TimeUnit a = values[i];
            TimeUnit b = ConvertTool.convert(TimeUnit.class, a.name());
            Assertions.assertEquals(a, b);
        }
    }

    @Test
    public void str2List(){
        String str = "a,b,c";

        List<String> list = Convert.convert(new TypeReference<>() {
        }, str);

        System.out.println(list);
    }

}
