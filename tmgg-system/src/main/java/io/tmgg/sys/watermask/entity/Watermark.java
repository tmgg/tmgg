package io.tmgg.sys.watermask.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Remark("页面水印")
@Entity
@Getter
@Setter
@Table(name = "sys_watermark")
public class Watermark extends BaseEntity {

    @Column(unique = true)
    @Remark("页面路径")
    String path;

    @NotNull
    @Remark("是否启用")
    Boolean enable;
}
