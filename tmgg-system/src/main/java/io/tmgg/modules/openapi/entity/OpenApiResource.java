package io.tmgg.modules.openapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
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
@Table(name = "open_api_resource")
public class OpenApiResource extends BaseEntity {

    @Column(length = DBConstants.LEN_NAME,unique = true)
    String name;

    @Column(length = DBConstants.LEN_NAME,unique = true)
    String action;

    @Column(name = "_desc")
    String desc;

    @Column(length = DBConstants.LEN_NAME,unique = true)
    String beanName;

    @JsonIgnore
    @OneToMany(mappedBy = "resource",cascade = CascadeType.ALL,orphanRemoval=true)
    List<OpenApiResourceArgument> parameterList;

    @JsonIgnore
    @OneToMany(mappedBy = "resource",cascade = CascadeType.ALL,orphanRemoval=true)
    List<OpenApiResourceArgumentReturn> returnList;


    String returnType;


    @Transient
    @JsonIgnore
    Object bean;

    @Transient
    @JsonIgnore
    Method method;
}
