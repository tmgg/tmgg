package io.tmgg.kettle.entity;


import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.converter.BaseToListConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.annotation.Lazy;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@FieldNameConstants
public class KettleFile extends BaseEntity {

    @Column(length = 54)
    String fileName;

    @Column(length = 3)
    String fileType;

    @Column(length = 50, unique = true)
    String name;

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
