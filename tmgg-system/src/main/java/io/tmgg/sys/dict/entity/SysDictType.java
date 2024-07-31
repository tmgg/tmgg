
package io.tmgg.sys.dict.entity;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 系统字典类型表
 *

 *
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDictType extends BaseEntity {


    /**
     * 名称
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 编码
     */
    @Column(nullable = false, unique = true)
    private String code;


    // 是否系统内置
    private Boolean embed;

    @Override
    public void prePersist() {
        super.prePersist();
    }


    @Override
    public void preUpdate() {
        super.preUpdate();
    }
}
