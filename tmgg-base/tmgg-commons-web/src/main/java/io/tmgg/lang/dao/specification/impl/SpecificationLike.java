package io.tmgg.lang.dao.specification.impl;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.specification.ExpressionTool;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

public class SpecificationLike<T> implements Specification<T> {

    private String fieldName; // 属性名
    private String value;     // 对应值

    public SpecificationLike(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
        Assert.hasText(value,"值不能为空");
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
        Expression<T> expression = ExpressionTool.getExpression(fieldName, root);

        String likeValue = value.contains("%") ? value : "%" + value + "%";
        return builder.like((Expression<String>) expression, likeValue);

    }
}
