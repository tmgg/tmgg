
package io.tmgg.modules.sys.entity;

import io.tmgg.framework.dict.DictField;
import io.tmgg.lang.ann.Msg;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.TreeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统组织机构表
 */
@Msg("组织机构")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysOrg extends BaseEntity implements TreeEntity<SysOrg> {

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


    @Column(nullable = false)
    private Boolean enabled;


    @NotNull
    @DictField(code ="orgType", label="机构类型", items = "10=单位 20=部门")
    private Integer type;


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
        return type == OrgType.DEPT;
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
