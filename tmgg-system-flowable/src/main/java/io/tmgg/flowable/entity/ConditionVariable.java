package io.tmgg.flowable.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConditionVariable implements Serializable {

    String name;
    String label;
    Object value;


    // 不设置 valueType 则为仅仅显示
    ValueType valueType;
    String params;

    boolean disabled;
    boolean visible = true;

    public enum ValueType {
        text,  digit
    }


    public ConditionVariable() {

    }



    public  ConditionVariable(String name, String label,ValueType valueType){
        this.name = name;
        this.label = label;
        this.valueType= ValueType.text;
    }



}
