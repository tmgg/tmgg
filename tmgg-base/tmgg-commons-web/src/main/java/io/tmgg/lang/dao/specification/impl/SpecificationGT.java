package io.tmgg.lang.dao.specification.impl;

import io.tmgg.lang.dao.specification.ExpressionTool;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationGT<T> implements Specification<T> {

    private  String fieldName; // 属性名
    private  Object value;     // 对应值

    public SpecificationGT(String fieldName, Object value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
        Expression expression = ExpressionTool.getExpression(fieldName, root);

        return builder.greaterThan(expression, (Comparable) value);
    }
}
