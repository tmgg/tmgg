package io.tmgg.modules.dev.code.bean;

import lombok.Data;

import java.util.List;

@Data
public class FieldInfo {
    String title;
    String name;
    String comment;
    String className;
    List<String> ann;
    String type;
    String text;


    boolean dict;
    String dictTypeCode;

    boolean hideInTable;
    boolean hideInForm;
    boolean hideInSearch;

    boolean required;
    // remark注解
    String annRemark;


}

