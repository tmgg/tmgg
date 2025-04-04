package io.tmgg.lang.dao.id;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.*;

@IdGeneratorType(CustomIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface CustomId {
    String prefix() default "";



}