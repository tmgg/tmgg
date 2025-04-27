
package io.tmgg.core.enums;

import io.tmgg.framework.dict.Dict;
import io.tmgg.framework.dict.DictItem;

/**
 * 性别常量
 */
@Dict(code = "sex",label = "性别")
public interface Sex {

    @DictItem(label = "保密")
    int NONE = 0;

    @DictItem(label ="男")
    int MAN = 1;

    @DictItem(label ="女")
    int WOMAN = 2;

}
