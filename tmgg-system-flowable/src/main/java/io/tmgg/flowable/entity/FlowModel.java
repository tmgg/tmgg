package io.tmgg.flowable.entity;

import cn.moon.lang.json.JsonTool;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.tomcat.util.json.JSONParser;

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


    public static void main(String[] args) {
        FlowModel flowModel = new FlowModel();
        flowModel.setId("1");
        flowModel.setName("请假流程");
        flowModel.setFormLink("http://www.ztchuntai.com");
        flowModel.getConditionVariableList().add(new ConditionVariable("level", "等级", ConditionVariable.ValueType.digit));

        System.out.println(JsonTool.toJsonQuietly(flowModel));
    }



}
