package io.tmgg.modules.asset.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.base.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Msg("素材类型")
@Getter
@AllArgsConstructor
public enum SysAssetType implements DictEnum {

    IMAGE("图片"),
    VIDEO("视频"),
    TEXT("文本"),
    DIR("文件夹"),;


    String message;

}
