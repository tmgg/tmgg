package io.tmgg.lang.dao.specification;

import io.tmgg.lang.dao.specification.expression.ExampleExpression;
import io.tmgg.lang.dao.specification.expression.LogicalExpression;
import io.tmgg.lang.dao.specification.expression.Operator;
import io.tmgg.lang.dao.specification.expression.SimpleExpression;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 查询条件
 * or 一般使用 contains anyXX 方法， 复杂点的可
 * LogicalExpression logicalExpression = new LogicalExpression(Operator.AND);
 * logicalExpression.addCriterion(new SimpleExpression(column, val1, Operator.GTE));
 * logicalExpression.addCriterion(new SimpleExpression(column, val2, Operator.LTE));
 * query.add(logicalExpression)
 * 多对多可 可使用 containsAnyMember
 *
 */
public class JpaQuery<T> implements Specification<T> {

    private final List<Specification> specificationList = new ArrayList<>();


    public static <X> JpaQuery<X> create() {
        return new JpaQuery<X>();
    }


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (specificationList.isEmpty()) {
            return builder.conjunction();
        }

        if (specificationList.size() == 1) {
            return specificationList.get(0).toPredicate(root, query, builder);
        }

        List<Predicate> predicates = new ArrayList<>();
        for (Specification c : specificationList) {
            Predicate predicate = c.toPredicate(root, query, builder);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        // 将所有条件用 and 联合起来
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    public long size() {
        return specificationList.size();
    }


    public JpaQuery likeExample(T t) {
        this.add(new ExampleExpression<>(t));
        return this;
    }

    public JpaQuery likeExample(T t, String... ignores) {
        this.add(new ExampleExpression<>(t, ignores));
        return this;
    }



    public JpaQuery eq(String column, Object value) {
        //this.add(new SimpleExpression(column, val, Operator.EQ));

        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);
                return value == null ? builder.isNull(expression) : builder.equal(expression, value);
            }
        });

        return this;
    }

    public JpaQuery isNull(String column) {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);
                return builder.isNull(expression);
            }
        });

        return this;
    }

    public JpaQuery isNotNull(String column) {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);
                return builder.isNotNull(expression);
            }
        });

        return this;
    }

    /**
     * key 为列表是， value为普通对象使用 isMember
     *
     * @param column
     * @param val
     * @return
     */
    public JpaQuery isMember(String column, Object val) {
        this.add(new SimpleExpression(column, val, Operator.IS_MEMBER));
        return this;
    }

    public JpaQuery isNotMember(String column, Object val) {
        this.add(new SimpleExpression(column, val, Operator.IS_NOT_MEMBER));
        return this;
    }



    public JpaQuery<T> ne(String column, Object val) {
        this.add(new SimpleExpression(column, val, Operator.NE));
        return this;
    }



    public JpaQuery<T> gt(String column, Object val) {
        this.add(new SimpleExpression(column, val, Operator.GT));
        return this;
    }


    public JpaQuery ge(String column, Object val) {
        this.add(new SimpleExpression<>(column, val, Operator.GTE));
        return this;
    }

    public JpaQuery<T> lt(String column, Object val) {
        this.add(new SimpleExpression<>(column, val, Operator.LT));
        return this;
    }

    public JpaQuery<T> le(String column, Object val) {
        this.add(new SimpleExpression<>(column, val, Operator.LTE));
        return this;
    }

    public JpaQuery between(String column, Object val1, Object val2) {
        LogicalExpression logicalExpression = new LogicalExpression(Operator.AND);
        logicalExpression.addCriterion(new SimpleExpression(column, val1, Operator.GTE));
        logicalExpression.addCriterion(new SimpleExpression(column, val2, Operator.LTE));
        this.add(logicalExpression);
        return this;
    }

    /**
     * ignore
     */
    public JpaQuery notBetween(String column, Object val1, Object val2) {
        LogicalExpression logicalExpression = new LogicalExpression(Operator.AND);
        logicalExpression.addCriterion(new SimpleExpression(column, val1, Operator.LT));
        logicalExpression.addCriterion(new SimpleExpression(column, val2, Operator.GT));
        this.add(logicalExpression);
        return this;
    }

    /**
     * ignore
     */
    public JpaQuery like(String column, Object val) {
        if (val == null) {
            this.eq(column, null);
        } else {
            String str = val.toString();

            val = str.contains("%") ? str : "%" + str + "%";
            this.add(new SimpleExpression(column, val, Operator.LIKE));
        }
        return this;
    }



    public JpaQuery<T> notLike(String column, Object val) {
        if (val == null) {
            this.eq(column, null);
        } else {
            String str = val.toString();
            val = str.contains("%") ? str : "%" + str + "%";
            this.add(new SimpleExpression<>(column, val, Operator.NOT_LIKE));
        }

        return this;
    }

    /**
     * ignore
     */
    public JpaQuery likeLeft(String column, Object val) {
        if (val == null) {
            this.eq(column, null);
        } else {
            String value = '%' + val.toString();
            this.add(new SimpleExpression(column, value, Operator.LIKE));
        }
        return this;
    }

    /**
     * ignore
     */
    public JpaQuery likeRight(String column, Object val) {
        if (val == null) {
            this.eq(column, null);
        } else {
            String val2 = val.toString() + '%';
            this.add(new SimpleExpression(column, val2, Operator.LIKE));
        }

        return this;
    }


    public JpaQuery<T> in(String column, Iterable<?> valueList) {
        List<Object> params = new ArrayList<>();
        if (valueList != null) {
            for (Object obj : valueList) {
                params.add(obj);
            }
        }

        return this.in(column, params.toArray());
    }

    public JpaQuery<T> notIn(String column, Iterable<?> valueList) {
        List<Object> objs = new ArrayList<>();
        for (Object obj : valueList) {
            objs.add(obj);
        }
        return this.notIn(column, objs.toArray());

    }

    public JpaQuery<T> in(String column, Object... valueList) {
        boolean hasValue = valueList != null && valueList.length > 0;

        if (!hasValue) {
            // in 空值， 相当于 1!=1, 直接没有数据返回了，这里用 is null and is not null
            this.eq(column, null);
            this.ne(column, null);
            return this;
        }


        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Expression expression = ExpressionTool.getExpression(column, root);
                CriteriaBuilder.In<Object> in = cb.in(expression);
                for (Object value : valueList) {
                    Assert.notNull(value, "in 中不应包含null");
                    in.value(value);
                }

                return in;
            }
        });
        return this;
    }


    public JpaQuery<T> notIn(String column, Object... valueList) {
        Assert.state(valueList != null && valueList.length > 0, "notIn 方法的参数应大于0");
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Expression expression = ExpressionTool.getExpression(column, root);
                CriteriaBuilder.In<Object> in = cb.in(expression);
                for (Object value : valueList) {
                    Assert.notNull(value, "not in 中不应包含null");
                    in.value(value);
                }

                // 注意，sql 中的not in 不会处理null值，这里or 一下
                return cb.or(in.not(), cb.isNull(expression));
            }
        });
        return this;
    }


    public JpaQuery<T> add(Specification<T> e) {
        if (e != null) {
            specificationList.add(e);
        }
        return this;
    }


    /**
     * 或查询
     * 形如： (a =1 or b =2 or c！=5)
     *
     * @param subQuery 子查询
     * @return 自己
     */
    public JpaQuery<T> any(Consumer<JpaQuery<T>> subQuery) {
        JpaQuery<T> wrapper = new JpaQuery<>();

        subQuery.accept(wrapper);


        LogicalExpression<T> logicalExpression = new LogicalExpression<>(Operator.OR);

        for (Specification specification : wrapper.specificationList) {
            logicalExpression.addCriterion(specification);
        }
        this.add(logicalExpression);

        return this;
    }

    public JpaQuery<T> any(Specification<T>... specifications) {
        LogicalExpression<T> logicalExpression = new LogicalExpression<>(Operator.OR);

        for (Specification specification : specifications) {
            logicalExpression.addCriterion(specification);
        }
        this.add(logicalExpression);

        return this;
    }

    public JpaQuery<T> like(Map<String, Object> param) {
        Set<Map.Entry<String, Object>> entries = param.entrySet();
        for (Map.Entry<String, Object> e : entries) {
            String key = e.getKey();
            Object value = e.getValue();

            if (value != null) {
                if (value instanceof String) {
                    this.like(key, value);
                } else {
                    this.eq(key, value);
                }
            }

        }
        return this;
    }

    // 去重复
    public JpaQuery<T> distinct() {
        this.add((Specification<T>) (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return criteriaBuilder.conjunction();
        });
        return this;
    }


    // 查群关联对象的某个字段
    public JpaQuery<T> containsAnyMembers(String fieldName, Iterable<?> value) {
        LogicalExpression logicalExpression = new LogicalExpression(Operator.OR);
        for (Object obj : value) {
            logicalExpression.addCriterion(new SimpleExpression(fieldName, obj, Operator.IS_MEMBER));
        }
        this.add(logicalExpression);
        return this;
    }


    public JpaQuery<T> notContainsAnyMembers(String fieldName, Object... value) {
        LogicalExpression logicalExpression = new LogicalExpression(Operator.OR);

        for (Object obj : value) {
            logicalExpression.addCriterion(new SimpleExpression(fieldName, obj, Operator.IS_NOT_MEMBER));
        }

        this.add(logicalExpression);
        return this;
    }

}
