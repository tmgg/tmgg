package io.tmgg.modules.asset.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import io.tmgg.modules.sys.entity.SysFile;
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
    @Column(columnDefinition = DBConstants.TYPE_LONGTEXT)
    @Msg("内容")
    @Lob
 //   @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String content;


    @Column(length = 20)
    @Msg("尺寸")
    String dimension;




}
