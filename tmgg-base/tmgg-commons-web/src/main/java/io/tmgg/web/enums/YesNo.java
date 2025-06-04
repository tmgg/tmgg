
package io.tmgg.web.enums;

import io.tmgg.lang.ann.Remark;
import lombok.Getter;

/**
 * 是或否的枚举
 */
@Remark("是否")
public enum YesNo  {

    @Remark("是")
    Y,

    @Remark("否")
    N
}
