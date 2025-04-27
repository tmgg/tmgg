
package io.tmgg.web.enums;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.base.DictEnum;
import lombok.Getter;

/**
 * 菜单类型枚举
 */
@Getter
@Msg("菜单类型")
public enum MenuType implements DictEnum {


    DIR(0, "目录"),
    MENU(1, "菜单"),
    BTN(2, "按钮");

    private final Integer code;

    private final String message;

    MenuType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


}
