package io.tmgg.modules.sys.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.base.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Msg("数据权限类型")
@Getter
@AllArgsConstructor
public enum DataPermType implements DictEnum {

    ALL("所有"),

    LEVEL("本级"),
    CHILDREN("本级和子级"),
    CUSTOM("自定义");

    String message;
}
