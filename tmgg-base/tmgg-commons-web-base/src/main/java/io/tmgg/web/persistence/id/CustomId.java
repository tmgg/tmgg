package io.tmgg.web.persistence.id;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.*;

/**
 * 该注解可以放到类型上， id字段上。
 *
 * 如果实体存在父类， 可放到类型上覆盖
 *
 * TODO 再建一个IdGeneratorDesc注解放到类上， 本注解简化
 *
 */
@IdGeneratorType(IdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD,ElementType.TYPE})
public @interface CustomId {

    /**
     * 例如：USER_2025040500383074634b0215f9a4
     * @return
     */
    String prefix() default "";


    /**
     * 样式
     *
     * @return
     */
    Style style() default Style.UUID;

    int length() default 32;


    enum Style {

        UUID,

        /**
         * 例如：2025040500383074634b0215f9a44
         */
        DATETIME_UUID,

        /**
         * 例如：
         * 2025040508583662400000000001，
         * 2025040508583670900000000002
         */
        DATETIME_SEQ,

        /**
         * 每日的序号会重置, 数据库中（否则应用重启会id重复）。
         *
         * 例如：
         * 2025040600001
         * 2025040600002
         */
        DAILY_SEQ,


    }
}
