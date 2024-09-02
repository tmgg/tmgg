
package io.tmgg.core.enums;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.DictEnum;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
@Remark("性别")
public enum Sex implements DictEnum {

    /**
     * 男
     */
    MAN(1, "男"),

    /**
     * 女
     */
    WOMAN(2, "女"),

    /**
     * 未知
     */
    NONE(3, "保密");

    private final Integer code;

    private final String message;

    Sex(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
