
package io.tmgg.sys.role.entity;

import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.converter.ToListConverter;
import io.tmgg.lang.dao.converter.ToMapStringObjectConverter;
import io.tmgg.web.enums.CommonStatus;
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
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CommonStatus status;


    /**
     * 权限码列表
     */
    @Convert(converter = ToListConverter.class)
    @Column(length = 10000)
    private List<String> perms;


    public SysRole(String id) {
        this.id = id;
    }
    public SysRole() {

    }
}
