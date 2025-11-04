
package io.tmgg.modules.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.ann.Remark;
import io.tmgg.data.domain.BaseEntity;
import io.tmgg.data.converter.ToListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 系统角色表
 */
@Remark("系统角色")
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysRole extends BaseEntity {

    @Excel(name = "角色名称")
    @Remark("名称")
    @Column(length = 50, unique = true)
    private String name;


    @Excel(name = "编码")
    @Remark("编码")
    @Column(unique = true, length = 20)
    private String code;

    @Excel(name = "排序")
    @Remark("排序")
    private Integer seq;


    @Excel(name = "备注")
    @Remark("备注")
    private String remark;

    @Excel(name = "启用")
    @Remark("启用")
    @Column(nullable = false)
    private Boolean enabled;


    @Remark("权限码")
    @Convert(converter = ToListConverter.class)
    @Column(length = 10000)
    private List<String> perms;


    /**
     * 是否内置，内置的不让修改
     */
    @Remark("是否内置")
    @Column(nullable = false)
    Boolean builtin;


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false),
            joinColumns = @JoinColumn(name = "role_id", nullable = false))
    Set<SysUser> users = new HashSet<>();


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_menu",
            inverseJoinColumns = @JoinColumn(name = "menu_id", nullable = false),
            joinColumns = @JoinColumn(name = "role_id", nullable = false))
    List<SysMenu> menus = new ArrayList<>();



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
