package io.tmgg.lang.dao.specification.impl;

import io.tmgg.lang.dao.specification.ExpressionTool;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationNE<T> implements Specification<T> {

    private  String fieldName; // 属性名
    private  Object value;     // 对应值

    public SpecificationNE(String fieldName, Object value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
        Expression<T> expression = ExpressionTool.getExpression(fieldName, root);

        return value == null ? builder.isNotNull(expression) : builder.notEqual(expression, value);
    }
}