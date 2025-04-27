package io.tmgg.framework.dict;


import java.lang.annotation.*;

/**
 * 定义一个类或接口是否生成系统字典（写到数据库sys_dict, sys_dict_item）
 * 需配合@Remark注解使用
 * 可参考 Sex.java
 */
@Deprecated
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE,ElementType.METHOD})
public @interface Dict {


    String code();

    String label();

    /**
     * 字典值是否数字， 如1，2，3
     * @return
     */
    boolean isNumber() default true;

}
