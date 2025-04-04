package io.tmgg.lang.dao.id;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.*;

@IdGeneratorType(CustomIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface CustomId {

    /**
     * 例如：USER_2025040500383074634b0215f9a4
     * @return
     */
    String prefix() default "";


    /**
     * 是否使用时间格式
     *
     * @return
     */
    IdStyle style() default IdStyle.UUID;

    int length() default 32;







}