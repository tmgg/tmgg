
package io.tmgg.sys.entity;

import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import io.tmgg.lang.dao.converter.ToListConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;


/**
 * 系统角色表
 */
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysRole extends BaseEntity {



    /**
     * 名称
     */
    @Column(length = 50, unique = true)
    private String name;

    /**
     * 编码
     */
    @Column(unique = true, length = 20)
    private String code;

    /**
     * 排序
     */
    private Integer seq;


    /**
     * 备注
     */
    private String remark;

    @Column(nullable = false, columnDefinition = DBConstants.COLUMN_DEFINITION_BOOLEAN_DEFAULT_TRUE)
    private Boolean enabled;

    /**
     * 权限码列表
     */
    @Convert(converter = ToListConverter.class)
    @Column(length = 10000)
    private List<String> perms;


    /**
     * 是否内置，内置的不让修改
     */
    @Column(nullable = false)
    Boolean builtin;



    public SysRole(String id) {
        this.id = id;
    }
    public SysRole() {

    }
}
