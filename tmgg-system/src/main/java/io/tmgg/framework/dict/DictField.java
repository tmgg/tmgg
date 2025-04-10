package io.tmgg.framework.dict;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DictField {

    String label();

    String code();




    int [] value();
    String[] valueLabel();

}
