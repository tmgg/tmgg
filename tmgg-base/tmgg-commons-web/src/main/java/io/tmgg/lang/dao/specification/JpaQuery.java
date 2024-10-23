package io.tmgg.lang.dao.specification;

import io.tmgg.lang.dao.specification.impl.*;
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
 * 多对多可 可使用 containsAnyMember
 */
public class JpaQuery<T> implements Specification<T> {

    private final List<Specification<T>> specificationList = new ArrayList<>();


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (specificationList.isEmpty()) {
            return builder.conjunction();
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
        return this.add(new SpecificationExample<>(t));
    }

    public JpaQuery likeExample(T t, String... ignores) {
        return this.add(new SpecificationExample<>(t, ignores));
    }


    public JpaQuery eq(String column, Object value) {
        return this.add(new SpecificationEQ<>(column, value));
    }

    public JpaQuery isNull(String column) {
        return this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);
                return builder.isNull(expression);
            }
        });

    }

    public JpaQuery isNotNull(String column) {
        return this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);
                return builder.isNotNull(expression);
            }
        });

    }


    public JpaQuery<T> ne(String column, Object val) {
        return this.add(new SpecificationNE<>(column, val));
    }


    public JpaQuery<T> gt(String column, Object val) {
        return this.add(new SpecificationGT<>(column, val));
    }


    public JpaQuery ge(String column, Object val) {
        return this.add(new SpecificationGTE<>(column, val));
    }

    public JpaQuery<T> lt(String column, Object val) {
        return this.add(new SpecificationLT<>(column, val));
    }

    public JpaQuery<T> le(String column, Object val) {
        return this.add(new SpecificationLTE<>(column, val));
    }

    public JpaQuery<T> between(String column, Object v1, Object v2) {
        this.ge(column, v1);
        this.le(column, v2);
        return this;
    }

    public JpaQuery<T> notBetween(String column, Object v1, Object v2) {
        this.lt(column, v1);
        this.gt(column, v2);
        return this;
    }


    public JpaQuery<T> like(String column, String val) {
        return this.add(new SpecificationLike<>(column, val));
    }


    public JpaQuery<T> notLike(String column, String val) {
        return this.add(new SpecificationNotLike<>(column, val));
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
            // in 空值， 相当于 1!=1, 直接没有数据返回了
            return this.add(new SpecificationAlwaysFalse<>());
        }


        return this.add(new Specification<T>() {
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
    }


    public JpaQuery<T> notIn(String column, Object... valueList) {
        boolean hasValue = valueList != null && valueList.length > 0;
        if (!hasValue) {
            return null;
        }

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
     * @param subQueryConsumer 子查询
     * @return 自己
     */
    public JpaQuery<T> or(Consumer<JpaQuery<T>> subQueryConsumer) {
        JpaQuery<T> orQuery = new JpaQuery<>();
        subQueryConsumer.accept(orQuery);
        return this.or(orQuery.specificationList);
    }

    public JpaQuery<T> or(List<Specification<T>> specifications) {
        return add((Specification<T>) (root, query, cb) -> {
            Predicate[] predicates = new Predicate[specifications.size()];
            for (int i = 0; i < specifications.size(); i++) {
                predicates[i] = specifications.get(i).toPredicate(root, query, cb);
            }

            return cb.or(predicates);
        });

    }

    public JpaQuery<T> like(Map<String, Object> param) {
        Set<Map.Entry<String, Object>> entries = param.entrySet();
        for (Map.Entry<String, Object> e : entries) {
            String key = e.getKey();
            Object value = e.getValue();

            if (value != null) {
                if (value instanceof String) {
                    this.like(key, (String) value);
                } else {
                    this.eq(key, value);
                }
            }

        }
        return this;
    }

    // 去重复
    public JpaQuery<T> distinct() {
        return this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                criteriaQuery.distinct(true);
                return cb.conjunction();
            }
        });
    }


    public JpaQuery<T> isMember(String k, Object v) {
        return this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path keyPath = root.get(k);
                return cb.isMember(v, keyPath);
            }
        });
    }
}
