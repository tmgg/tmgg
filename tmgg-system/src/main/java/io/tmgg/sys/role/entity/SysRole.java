
package io.tmgg.sys.role.entity;

import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Set;


/**
 * 系统角色表
 */
@Entity(name = "sys_role")
@Getter
@Setter
@FieldNameConstants
public class SysRole extends BaseEntity {

    public static final String DEFAULT_ROLE = "SYS_DEFAULT_ROLE"; // 默认角色


    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    @Column(unique = true)
    private String code;

    /**
     * 排序
     */
    private Integer sort;


    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    @NotNull
    private CommonStatus status;


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "sys_role_menu",
            joinColumns = @JoinColumn(name = "role_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "menu_id", nullable = false, updatable = false))
    Set<SysMenu> menus;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "role_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, updatable = false))
    Set<SysUser> users;

    public SysRole(String id) {
        this.id = id;
    }
    public SysRole() {

    }
}
