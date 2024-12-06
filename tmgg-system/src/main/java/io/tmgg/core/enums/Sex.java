
package io.tmgg.core.enums;

import io.tmgg.framework.dict.Dict;
import io.tmgg.lang.ann.Remark;

/**
 * 性别常量
 */
@Dict(code = "SEX")
@Remark("性别")
public interface Sex {

    @Remark("保密")
    int NONE = 0;

    @Remark("男")
    int MAN = 1;

    @Remark("女")
    int WOMAN = 2;


}
