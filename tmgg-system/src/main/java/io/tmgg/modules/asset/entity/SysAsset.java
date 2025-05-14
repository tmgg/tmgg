package io.tmgg.modules.asset.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("素材")
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysAsset extends BaseEntity {

    String pid;

    @Msg("名称")
    @Column(length = 50)
    String name;

    @Msg("编码")
    @Column(length = 50,unique = true)
    String code;

    @Msg("类型")
    @Enumerated
    SysAssetType type;


    // 如果是文件，存id
    @Column(columnDefinition = DBConstants.TYPE_BLOB)
    @Msg("内容")
    @Lob
    String content;


    @Column(length = 20)
    @Msg("尺寸")
    String dimension;




}
