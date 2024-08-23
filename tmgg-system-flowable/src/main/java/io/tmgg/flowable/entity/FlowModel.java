package io.tmgg.flowable.entity;


import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.tmgg.lang.JsonTool;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@JsonRootName("flowModel")
@FieldNameConstants
public class FlowModel  {

    private String id;

    /**
     * 编码, 流程的key
     */
    @NotNull
    private String code;

    /**
     * 名称
     */
    private String name;


    @JacksonXmlProperty(localName = "conditionVariable")
    @JacksonXmlElementWrapper(localName = "conditionVariableList")
    private List<ConditionVariable> conditionVariableList = new ArrayList<>();


    private String content;


    private String formLink;






}
