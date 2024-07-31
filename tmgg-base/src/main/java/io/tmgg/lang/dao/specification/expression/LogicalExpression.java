package io.tmgg.lang.dao.specification.expression;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 逻辑条件表达式 用于复杂条件时使用，如但属性多对应值的OR查询等
 *
 * @author lee
 */
public class LogicalExpression<T> implements Specification<T> {
    private final List<Specification> criterionList = new ArrayList<>(); // 逻辑表达式中包含的表达式
    private final Operator operator; // 计算符


    public LogicalExpression(Operator operator) {
        this.operator = operator;
    }

    public LogicalExpression(Specification[] expressions, Operator operator) {
        this.operator = operator;
        for (Specification c: expressions) {
            if (c != null) {
                criterionList.add(c);
            }
        }
    }

    public LogicalExpression(List<Specification> expressions, Operator operator) {
        this.operator = operator;
        for (Specification c: expressions) {
            if (c != null) {
                criterionList.add(c);
            }
        }
    }

    public LogicalExpression<T> addCriterion(Specification criterion) {
        this.criterionList.add(criterion);
        return this;
    }


    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        for (Specification c : criterionList) {
            if (c != null) {
                predicates.add(c.toPredicate(root, query, builder));
            }
        }

        if (predicates.isEmpty()) {
            return null;
        }

        switch (operator) {
            case OR:
                return builder.or(predicates.toArray(new Predicate[0]));
            case AND:
                return builder.and(predicates.toArray(new Predicate[0]));
            default:
                return null;
        }
    }


}
