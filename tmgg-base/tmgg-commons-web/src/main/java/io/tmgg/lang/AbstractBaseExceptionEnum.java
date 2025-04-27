
package io.tmgg.lang;

/**
 * 异常枚举格式规范
 *
 *
 */
public interface AbstractBaseExceptionEnum {

    /**
     * 获取异常的状态码
     *
     * @return 状态码
     *
     */
    int getCode();

    /**
     * 获取异常的提示信息
     *
     * @return 提示信息
     *
     */
    String getMessage();

}
