package io.tmgg.modules.asset.entity;

import io.tmgg.framework.dict.DictField;
import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("素材")
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysAsset extends BaseEntity {



    @ManyToOne(fetch = FetchType.EAGER)
    SysAssetDir dir;

    @Remark("名称")
    @Column(length = 50,unique = true)
    String name;


    @NotNull
    @Remark("类型")
    @DictField(label = "素材类型",code = "assetType", items = "0-富文本,1-文件")
    Integer type;


    // 如果是文件，存id
    @Column(columnDefinition = DBConstants.TYPE_BLOB)
    @Remark("内容")
    @Lob
    String content;


    @Column(length = 20)
    @Remark("尺寸")
    String dimension;




}
