
package io.tmgg.sys.entity;

import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDict extends BaseEntity {


    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;


    // 是否系统内置
    @NotNull
    private Boolean builtin;

}
