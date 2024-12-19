package io.tmgg.lang.dao.specification;

import cn.hutool.core.util.StrUtil;
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


    public void likeExample(T t) {
        this.add(new SpecificationExample<>(t));
    }

    public void likeExample(T t, String... ignores) {
        this.add(new SpecificationExample<>(t, ignores));
    }

    /**
     * 常见的搜索，会模糊匹配多个字段
     *
     * @param searchText 待搜索的文本
     */
    public void searchText(String searchText, String... columns) {
        if (StrUtil.isBlank(searchText)) {
            return;
        }
        this.or(qq -> {
            for (String column : columns) {
                qq.like(column, searchText);
            }
        });
    }

    public void eq(String column, Object value) {
        this.add(new SpecificationEQ<>(column, value));
    }

    public void eqIf(boolean state, String column, Object value) {
        if (state) {
            this.add(new SpecificationEQ<>(column, value));
        }
    }

    public void isNull(String column) {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);
                return builder.isNull(expression);
            }
        });

    }

    public void isNotNull(String column) {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);
                return builder.isNotNull(expression);
            }
        });

    }


    public void ne(String column, Object val) {
        this.add(new SpecificationNE<>(column, val));
    }


    public void gt(String column, Object val) {
        this.add(new SpecificationGT<>(column, val));
    }


    public void ge(String column, Object val) {
        this.add(new SpecificationGTE<>(column, val));
    }

    public void lt(String column, Object val) {
        this.add(new SpecificationLT<>(column, val));
    }

    public void le(String column, Object val) {
        this.add(new SpecificationLTE<>(column, val));
    }

    public void between(String column, Object v1, Object v2) {
        this.ge(column, v1);
        this.le(column, v2);
    }

    public void notBetween(String column, Object v1, Object v2) {
        this.lt(column, v1);
        this.gt(column, v2);
    }


    public void like(String column, String val) {
        this.add(new SpecificationLike<>(column, val));
    }

    public void likeIf(boolean state, String column, String val) {
        if (state) {
            this.add(new SpecificationLike<>(column, val));
        }
    }

    public void notLike(String column, String val) {
        this.add(new SpecificationNotLike<>(column, val));
    }


    public void in(String column, Iterable<?> valueList) {
        List<Object> params = new ArrayList<>();
        if (valueList != null) {
            for (Object obj : valueList) {
                params.add(obj);
            }
        }

        this.in(column, params.toArray());
    }

    public void notIn(String column, Iterable<?> valueList) {
        List<Object> objs = new ArrayList<>();
        for (Object obj : valueList) {
            objs.add(obj);
        }
        this.notIn(column, objs.toArray());
    }

    public void in(String column, Object... valueList) {
        boolean hasValue = valueList != null && valueList.length > 0;

        if (!hasValue) {
            // in 空值， 相当于 1!=1, 直接没有数据返回了
            this.add(new SpecificationAlwaysFalse<>());
            return;
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
    }


    public void notIn(String column, Object... valueList) {
        boolean hasValue = valueList != null && valueList.length > 0;
        if (!hasValue) {
            return;
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
    }


    public void add(Specification<T> e) {
        if (e != null) {
            specificationList.add(e);
        }
    }


    /**
     * 或查询
     * 形如： (a =1 or b =2 or c！=5)
     *
     * @param subQueryConsumer 子查询
     */
    public void or(Consumer<JpaQuery<T>> subQueryConsumer) {
        JpaQuery<T> orQuery = new JpaQuery<>();
        subQueryConsumer.accept(orQuery);
        this.or(orQuery.specificationList);
    }

    public void or(List<Specification<T>> specifications) {
        add((Specification<T>) (root, query, cb) -> {
            Predicate[] predicates = new Predicate[specifications.size()];
            for (int i = 0; i < specifications.size(); i++) {
                predicates[i] = specifications.get(i).toPredicate(root, query, cb);
            }

            return cb.or(predicates);
        });

    }

    public void like(Map<String, Object> param) {
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
    }

    // 去重复
    public void distinct() {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                criteriaQuery.distinct(true);
                return cb.conjunction();
            }
        });
    }


    public void isMember(String k, Object v) {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path keyPath = root.get(k);
                return cb.isMember(v, keyPath);
            }
        });
    }
}
