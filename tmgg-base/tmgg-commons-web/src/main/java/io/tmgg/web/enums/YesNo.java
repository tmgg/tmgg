
package io.tmgg.web.enums;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.base.DictEnum;
import lombok.Getter;

/**
 * 是或否的枚举
 *
 *
 *
 */
@Getter
@Msg("是否")
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
