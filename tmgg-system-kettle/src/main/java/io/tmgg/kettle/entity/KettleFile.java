package io.tmgg.kettle.entity;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.converter.BaseToListConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
public class KettleFile extends BaseEntity {

    String fileName;

    @NotNull
    @Column(unique = true)
    String jobName;

    @NotNull
    String description;

    @Lazy
    @Lob
    String content;

    @Lob
    @Convert(converter = ParameterListConverter.class)
    List<Parameter> parameterList;

    @Getter
    @Setter
    public static class Parameter {
        String name;
        String defaultValue;
        String description;
    }

    public static class ParameterListConverter extends BaseToListConverter<Parameter>{}
}
