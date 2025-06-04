
package io.tmgg.framework.enums;

import io.tmgg.lang.ann.Remark;

/**
 * 性别常量
 */
@Remark("性别常量")
public enum Sex {

    @Remark("男")
    MALE,

    @Remark ("女")
    FEMALE,

    @Remark("未知")
    UNKNOWN,

    @Remark("其他")
    OTHER;

}
