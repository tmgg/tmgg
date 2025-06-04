package io.tmgg.framework.dict;

import java.lang.annotation.*;


/**
 * 定义字段为数据字典
 *
 * 在字段上增加 @DictField 注解
 *
 * @gendoc
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DictField {

    String label();

    String code();


    /**
     * 键值对， 格式为: 1-男,2-女
     * @return
     */
    String items();


}
