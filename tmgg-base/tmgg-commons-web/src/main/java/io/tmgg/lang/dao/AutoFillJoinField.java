package io.tmgg.lang.dao;


import java.lang.annotation.*;

/**
 * 关联字段自动填充字段， 使用了缓存策略
 * 数据量小的表， 如 机构， 仓库， 明细表最好不要用
 * 像数据量大的表不合适
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AutoFillJoinField {

    String sourceField();



    // 关联实体
    Class<? extends BaseEntity> joinEntity();

    // 关联字段
    String joinField() default "id";

    // 查询结果
    String returnField();


}
