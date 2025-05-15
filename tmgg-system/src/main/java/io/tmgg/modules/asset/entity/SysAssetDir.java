package io.tmgg.modules.asset.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("素材文件夹")
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysAssetDir extends BaseEntity {


    @Msg("名称")
    @Column(length = 50)
    String name;


    @Msg("编码")
    String code;


}
