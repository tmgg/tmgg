package io.tmgg.flowable.admin.entity;


import io.tmgg.data.domain.BaseEntity;
import io.tmgg.data.converter.BaseToListConverter;
import io.tmgg.lang.ann.Remark;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

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
    @Remark( "编码")
    @NotNull
    private String code;


    @Remark( "名称")
    private String name;


    @Column(length = 10000)
    @Convert(converter = ConditionVariableConverter.class)
    private List<ConditionVariable> conditionVariableList = new ArrayList<>();


    @Column(columnDefinition = "blob")
    private String content;


    @Convert(converter = FormKeyConverter.class)
    private List<FormKey> formKeyList = new ArrayList<>();




    public static class ConditionVariableConverter extends BaseToListConverter<ConditionVariable>{}

    public static class FormKeyConverter extends BaseToListConverter<FormKey>{}


}
