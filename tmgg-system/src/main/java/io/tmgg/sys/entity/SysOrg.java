
package io.tmgg.sys.entity;

import io.tmgg.lang.TreeDefinition;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
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


    @Column(nullable = false, columnDefinition = DBConstants.COLUMN_DEFINITION_BOOLEAN_DEFAULT_TRUE)
    private Boolean enabled;


    @NotNull
    @Enumerated(EnumType.STRING)
    private OrgType type;


    // 扩展字段1
    private String extra1;
    private String extra2;
    private String extra3;


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


    @Override
    public void prePersist() {
        super.prePersist();
        if(enabled == null){
            enabled = true;
        }
    }
}
