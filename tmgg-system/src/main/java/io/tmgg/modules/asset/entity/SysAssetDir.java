package io.tmgg.modules.asset.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("素材文件夹")
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysAssetDir extends BaseEntity {


    @NotNull
    @Remark("名称")
    @Column(length = 50, unique = true)
    String name;

    @NotNull
    @Remark("编码")
    @Column(unique = true)
    String code;


}
