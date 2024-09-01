
package io.tmgg.sys.entity;

import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;

/**
 * 系统字典值表
 *

 *
 */
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysDictItem extends BaseEntity {


    /**
     * 字典类型id
     */
    private String typeId;

    /**
     * 值
     */
    private String value;

    /**
     * 编码
     */
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
    private CommonStatus status;

    // 前段颜色
    private String color;

    // 是否系统内置
    @NotNull
    private Boolean builtin;


    @Override
    public void prePersist() {
        super.prePersist();
        status = status == null ? CommonStatus.ENABLE : status;
    }


    @Override
    public void preUpdate() {
        super.preUpdate();
        status = status == null ? CommonStatus.ENABLE : status;
    }
}
