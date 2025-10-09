package io.tmgg.modules.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import io.tmgg.web.persistence.converter.BaseToListConverter;
import io.tmgg.web.persistence.converter.ToListComplexConverter;
import io.tmgg.web.persistence.converter.ToListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.lang.reflect.Method;
import java.util.List;

@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "sys_api_resource")
public class ApiResource extends BaseEntity {

    @Excel(name = "接口名称")
    @Column(length = DBConstants.LEN_NAME,unique = true)
    String name;

    @Excel(name = "接口方法")
    @Column(length = 62,unique = true)
    String action;

    @Excel(name = "接口描述")
    @Column(name = "_desc")
    String desc;

    @Column(length = DBConstants.LEN_NAME)
    String beanName;

    @Lob
    @JsonIgnore
    @Convert(converter = C1.class)
    List<ApiResourceArgument> parameterList;

    @Lob
    @JsonIgnore
    @Convert(converter = C2.class)
    List<ApiResourceArgumentReturn> returnList;


    String returnType;


    @Transient
    @JsonIgnore
    Object bean;

    @Transient
    @JsonIgnore
    Method method;


    public static class C1 extends BaseToListConverter<ApiResourceArgument> {}
    public static class C2 extends BaseToListConverter<ApiResourceArgumentReturn> {}
}
