package io.tmgg.lang.dao.specification;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpressionTool {

    // 获得字段路径， 字段包含小数点 .
    public static Expression getExpression(String fieldName, Root<?> root) {
        // 普通字段
        if (!fieldName.contains(".")) {
            Attribute.PersistentAttributeType type = root.getModel().getAttribute(fieldName).getPersistentAttributeType();
            boolean isMany = type == Attribute.PersistentAttributeType.ONE_TO_MANY
                             || type == Attribute.PersistentAttributeType.MANY_TO_MANY;

            // boolean member = operator == Operator.IS_MEMBER || operator == Operator.IS_NOT_MEMBER;

            if (isMany /*&& !member*/) {
                return getOrCreateJoin(root, fieldName);
            } else {
                return root.get(fieldName);
            }
        }
        Expression expression = root;
        String[] names = fieldName.split("\\.");

        for (String name : names) {
            expression = ((Path) expression).get(name);
        }

        return expression;

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
}
