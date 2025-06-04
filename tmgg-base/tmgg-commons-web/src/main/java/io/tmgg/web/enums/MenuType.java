
package io.tmgg.web.enums;

import io.tmgg.lang.ann.Remark;
import lombok.Getter;

/**
 * 菜单类型枚举
 */
@Remark("菜单类型")
public enum MenuType  {


    @Remark("目录")
    DIR,

    @Remark("菜单")
    MENU,

    @Remark("按钮")
    BTN;

}
