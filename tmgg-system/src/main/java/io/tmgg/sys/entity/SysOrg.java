
package io.tmgg.sys.entity;

import io.tmgg.lang.TreeDefinition;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.web.enums.CommonStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统组织机构表
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysOrg extends BaseEntity implements TreeDefinition<SysOrg> {

    public SysOrg() {
    }

    public SysOrg(String id) {
        super(id);
    }

    /**
     * 父id, 如果是根节点，则为空
     */
    private String pid;


    /**
     * 名称
     */
    @NotNull
    private String name;



    /**
     * 排序
     */
    private Integer seq;


    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    @NotNull
    private CommonStatus status;


    @NotNull
    @Enumerated(EnumType.STRING)
    private OrgType type;


    // 预留字段1
    private String reservedField1;

    // 预留字段2
    private String reservedField2;

    // 预留字段3
    private String reservedField3;


    @Transient
    List<SysOrg> children;




    @Transient
    public boolean isDept() {
        if (type == null) {
            return false;
        }
        return type.isDept();
    }


    @Override
    public String toString() {
        return name;
    }
}
