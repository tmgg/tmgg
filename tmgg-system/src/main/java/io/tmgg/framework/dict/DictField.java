package io.tmgg.framework.dict;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DictField {

    String label();

    String code();


    /**
     * 键值对， 格式为: 1=男 2=女
     * @return
     */
    String kvs();


}
