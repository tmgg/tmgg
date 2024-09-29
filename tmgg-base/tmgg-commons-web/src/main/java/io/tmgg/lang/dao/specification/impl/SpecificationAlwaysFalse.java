package io.tmgg.lang.dao.specification.impl;

import io.tmgg.lang.dao.specification.ExpressionTool;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

/**
 * 相当于sql 中的  where 1！=1
 * @param <T>
 */
public class SpecificationAlwaysFalse<T> implements Specification<T> {




    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder cb) {
        Predicate alwaysFalse = cb.notEqual(cb.literal(1), cb.literal(1));
        return alwaysFalse;
    }
}
