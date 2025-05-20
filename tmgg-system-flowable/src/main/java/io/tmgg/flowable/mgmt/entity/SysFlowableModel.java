package io.tmgg.flowable.mgmt.entity;


import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.converter.BaseToListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
    @Excel(name = "编码")
    @NotNull
    private String code;


    @Excel(name = "名称")
    private String name;


    @Column(length = 10000)
    @Convert(converter = ConditionVariableConverter.class)
    private List<ConditionVariable> conditionVariableList = new ArrayList<>();


    @Column(columnDefinition = "blob")
    private String content;


    @Excel(name = "表单链接")
    private String formUrl;



    public static class ConditionVariableConverter extends BaseToListConverter<ConditionVariable>{}




}
