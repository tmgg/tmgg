
package io.tmgg.modules.sys.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Msg("数据字典")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDict extends BaseEntity {


    @Msg("名称")
    @Column(nullable = false, unique = true)
    private String name;

    @Msg("编码")
    @Column(nullable = false, unique = true)
    private String code;


    @Msg("系统内置")
    @NotNull
    private Boolean builtin;

    // 是否数字类型，如1，2，3
    @Msg("是否数字类型")
    private Boolean isNumber;

}
