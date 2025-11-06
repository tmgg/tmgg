package io.tmgg.flowable.definition;

/**
 * 表单描述
 * 一个流程可能有多个表单
 */
public @interface FormKeyDescription {

    //  表单的唯一标识
    String value();

    // 表单显示名称
    String label();
}
