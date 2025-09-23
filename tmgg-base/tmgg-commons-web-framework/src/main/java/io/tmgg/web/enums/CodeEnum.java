package io.tmgg.web.enums;

/**
 * 枚举代码接口
 * 所有需要支持通用转换的枚举都应实现此接口
 */
public interface CodeEnum {
    int getCode();
    String getMsg();
}
