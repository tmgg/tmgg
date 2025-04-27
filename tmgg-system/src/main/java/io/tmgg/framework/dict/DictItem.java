package io.tmgg.framework.dict;

import java.lang.annotation.*;


@Deprecated
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DictItem {

    String label();

}
