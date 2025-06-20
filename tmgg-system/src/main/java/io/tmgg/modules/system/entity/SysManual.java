package io.tmgg.modules.system.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("操作手册")
@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_sys_manual", columnNames = {"name","version"})})
@FieldNameConstants
public class SysManual extends BaseEntity {

    @NotNull
    @Remark("名称")
    @Column(length = 100)
    String name;

    @NotNull
    @Remark("版本")
    Integer version;

    @Remark("文件")
    @Column(length = 32)
    String fileId;

}
