
package io.tmgg.modules.sys.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Remark("数据字典")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDict extends BaseEntity {


    @Remark("名称")
    @Column(nullable = false, unique = true)
    private String name;

    @Remark("编码")
    @Column(nullable = false, unique = true)
    private String code;


    @Remark("系统内置")
    @NotNull
    private Boolean builtin;

    // 是否数字类型，如1，2，3
    @Remark("是否数字类型")
    private Boolean isNumber;

}
