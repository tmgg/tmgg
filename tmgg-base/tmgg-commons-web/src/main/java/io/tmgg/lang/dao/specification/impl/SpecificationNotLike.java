package io.tmgg.lang.dao.specification.impl;

import io.tmgg.lang.dao.specification.ExpressionTool;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationNotLike<T> implements Specification<T> {

    private String fieldName; // 属性名
    private String value;     // 对应值

    public SpecificationNotLike(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
        Expression<T> expression = ExpressionTool.getExpression(fieldName, root);
        if (value == null) {
            return builder.isNull(expression);
        }

        String likeValue = value.contains("%") ? value : "%" + value + "%";
        return builder.notLike((Expression<String>) expression, likeValue);

    }
}
