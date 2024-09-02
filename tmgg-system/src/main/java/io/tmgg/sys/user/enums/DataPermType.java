package io.tmgg.sys.user.enums;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Remark("数据权限类型")
@Getter
@AllArgsConstructor
public enum DataPermType implements DictEnum {

    ALL("所有"),

    ORG_ONLY("本级"),
    ORG_AND_CHILDREN("本级和子级"),
    CUSTOM("自定义");

    String message;
}
