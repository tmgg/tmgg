package io.tmgg.flowable.entity;


import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.converter.BaseToListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@FieldNameConstants
public class SysFlowableModel extends BaseEntity {


    /**
     * 编码, 流程的key
     */
    @NotNull
    private String code;


    private String name;


    @Column(length = 10000)
    @Convert(converter = ConditionVariableConverter.class)
    private List<ConditionVariable> conditionVariableList = new ArrayList<>();


    @Lob
    @Column(columnDefinition = "blob")
    private String content;


    private String formUrl;



    public static class ConditionVariableConverter extends BaseToListConverter<ConditionVariable>{}




}
