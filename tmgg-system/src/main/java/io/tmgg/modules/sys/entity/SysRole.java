
package io.tmgg.modules.sys.entity;

import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.ann.Msg;
import io.tmgg.persistence.BaseEntity;
import io.tmgg.persistence.converter.ToListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;


/**
 * 系统角色表
 */
@Msg("系统角色")
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysRole extends BaseEntity {




    @Excel(name = "角色名称")
    @Msg("名称")
    @Column(length = 50, unique = true)
    private String name;


    @Excel(name = "编码")
    @Msg("编码")
    @Column(unique = true, length = 20)
    private String code;

    @Excel(name = "排序")
    @Msg("排序")
    private Integer seq;


    @Excel(name = "备注")
    @Msg("备注")
    private String remark;

    @Excel(name = "启用")
    @Msg("启用")
    @Column(nullable = false)
    private Boolean enabled;


    @Msg("权限码")
    @Convert(converter = ToListConverter.class)
    @Column(length = 10000)
    private List<String> perms;


    /**
     * 是否内置，内置的不让修改
     */
    @Msg("是否内置")
    @Column(nullable = false)
    Boolean builtin;


    @Transient
    private List<String> menuIds;

    public SysRole(String id) {
        this.setId(id);
    }
    public SysRole() {

    }

    @Override
    public void prePersist() {
        super.prePersist();
        if(enabled == null){
            enabled = true;
        }
    }
}
