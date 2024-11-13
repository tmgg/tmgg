
package io.tmgg.web.enums;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.base.DictEnum;
import lombok.Getter;

/**
 * 是或否的枚举
 *
 *
 *
 */
@Deprecated
@Getter
@Remark("是否")
public enum YesNo implements DictEnum {
    Y("Y", "是"),
    N("N", "否");

    private final String code;
    private final String message;

    YesNo(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
