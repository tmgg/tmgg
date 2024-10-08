
package io.tmgg.core.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 数据范围类型枚举
 *
 */
@Getter
public enum DataScopeTypeEnum {

    /**
     * 全部数据
     */
    ALL(1, "全部数据"),

    /**
     * 本部门及以下数据
     */
    DEPT_WITH_CHILD(2, "本部门及以下数据"),

    /**
     * 本部门数据
     */
    DEPT(3, "本部门数据"),

    /**
     * 仅本人数据
     */
    SELF(4, "仅本人数据"),

    /**
     * 仅本人数据
     */
    DEFINE(5, "自定义数据");

    private final Integer code;

    private final String message;

    DataScopeTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DataScopeTypeEnum valueOf(Integer code) {
       return   Arrays.stream(DataScopeTypeEnum.values()).filter(t->t.getCode() == code).findFirst().orElse(null);
    }
}
