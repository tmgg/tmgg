package io.tmgg.flowable.admin.entity;

import lombok.Data;

/**
 * 表单描述
 * 一个流程可能有多个表单
 */
@Data
public class FormKey {

    //  表单的唯一标识
    String value;

    // 表单显示名称
    String label;
}
