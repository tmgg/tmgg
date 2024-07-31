package io.tmgg.lang.dao.specification.expression;


import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 简单条件表达式
 */
public class SimpleExpression<T> implements Specification<T> {

    private final String fieldName; // 属性名
    private final Object value;     // 对应值
    private final Operator operator;  // 计算符

    public SimpleExpression(String fieldName, Object value, Operator operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getValue() {
        return value;
    }

    public Operator getOperator() {
        return operator;
    }

    private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {
        for (Join<?, ?> join : from.getJoins()) {
            boolean sameName = join.getAttribute().getName().equals(attribute);

            if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
                return join;
            }
        }

        return from.join(attribute, JoinType.LEFT);
    }


    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Expression expression;
        if (fieldName.contains(".")) {
            String[] names = fieldName.split("\\.");
            expression = root;

            for (String name : names) {
                Class clazz = expression.getJavaType();
                if (clazz.equals(Set.class)) {
                    expression = root.joinSet(name);
                } else if (clazz.equals(List.class)) {
                    expression = root.joinList(name);
                } else if (clazz.equals(Map.class)) {
                    expression = root.joinMap(name);
                } else {
                    expression = ((Path) expression).get(name);
                }
            }
        } else {
            PersistentAttributeType type = root.getModel().getAttribute(fieldName).getPersistentAttributeType();
            boolean isMany = type == PersistentAttributeType.ONE_TO_MANY
                                   || type == PersistentAttributeType.MANY_TO_MANY;

            boolean member = operator == Operator.IS_MEMBER || operator == Operator.IS_NOT_MEMBER;

            if(isMany && !member){
                expression =  getOrCreateJoin(root, fieldName) ;
            }else {
                expression =  root.get(fieldName);
            }
        }


        switch (operator) {
            case EQ:
                return value == null ? builder.isNull(expression) : builder.equal(expression, value);
            case NE:
                return value == null ? builder.isNotNull(expression) : builder.notEqual(expression, value);
            case LIKE:
                return builder.like((Expression<String>) expression, value.toString());
            case NOT_LIKE:
                return builder.notLike((Expression<String>) expression, value.toString());
            case LT:
                return builder.lessThan(expression, (Comparable) value);
            case GT:
                return builder.greaterThan(expression, (Comparable) value);
            case LTE:
                return builder.lessThanOrEqualTo(expression, (Comparable) value);
            case GTE:
                return builder.greaterThanOrEqualTo(expression, (Comparable) value);
            case IS_MEMBER:
                return builder.isMember(value, expression);
            case IS_NOT_MEMBER:
                return builder.isNotMember(value, expression);
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return fieldName + " " + operator + " " + value;
    }

}
