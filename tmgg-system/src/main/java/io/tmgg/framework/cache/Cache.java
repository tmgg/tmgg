package io.tmgg.framework.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {


    // 缓存key的前缀
    String name() default "";
    
    // 缓存过期时间，单位秒，默认5分钟
    long expire() default 3600;
    

    

}