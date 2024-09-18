package io.tmgg.lang.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * IP 效验注解
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface IpValid {

	String message() default "IP地址不正确";
}
