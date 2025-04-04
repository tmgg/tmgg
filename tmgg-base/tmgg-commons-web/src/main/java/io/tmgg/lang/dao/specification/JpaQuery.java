package io.tmgg.lang.dao.specification;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.specification.impl.*;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 查询条件
 * <p>
 * 注意：如果键值对查询，值为空的情况下，会忽略
 */
public class JpaQuery<T> implements Specification<T> {

    private final List<Specification<T>> specificationList = new ArrayList<>();


    @NotNull
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (specificationList.isEmpty()) {
            return builder.conjunction();
        }

        List<Predicate> predicates = new ArrayList<>();
        for (Specification<T> c : specificationList) {
            Predicate predicate = c.toPredicate(root, query, builder);
            predicates.add(predicate);
        }

        // 将所有条件用 and 联合起来
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    public long size() {
        return specificationList.size();
    }

    public void clear(){
        specificationList.clear();
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
        final String trimText = searchText.trim();
        this.addSubOr(qq -> {
            for (String column : columns) {
                qq.like(column, trimText);
            }
        });
    }

    public void eq(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add((Specification<T>) (root, query, builder) -> {
            Expression expression = ExpressionTool.getExpression(column, root);
            return builder.equal(expression, v);
        });
    }

    public void isNull(String column) {
        this.add((Specification<T>) (root, query, builder) -> ExpressionTool.getExpression(column, root).isNull());
    }

    public void isNotNull(String column) {
        this.add((Specification<T>) (root, query, builder) -> ExpressionTool.getExpression(column, root).isNotNull());

    }


    public void ne(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new SpecificationNE<>(column, v));
    }


    public void gt(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new SpecificationGT<>(column, v));
    }


    public void ge(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new SpecificationGTE<>(column, v));
    }

    public void lt(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new SpecificationLT<>(column, v));
    }

    public void le(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new SpecificationLTE<>(column, v));
    }


    public void between(String column, Object v1, Object v2) {
        if (v1 == null && v2 == null) {
            return;
        }

        if (v1 != null && v2 != null) {
            this.addSubAnd(q -> {
                q.ge(column, v1);
                q.le(column, v2);
            });
            return;
        }

        if (v1 != null) {
            this.ge(column, v1);
        }else {
            this.le(column, v2);
        }
    }

    public void notBetween(String column, Object v1, Object v2) {
        if (v1 == null || v2 == null) {
            return;
        }
        this.addSubAnd(q -> {
            q.lt(column, v1);
            q.gt(column, v2);
        });
    }

    /**
     * 判断值是否在某个区间
     * 注意： 包含边界
     * 如当前日期是否在开始、结束时间之间
     *
     * @param column1 如开始时间
     * @param column2 如结束时间
     * @param v   当前时间
     */
    public void valueBetween(String column1, String column2, Object v) {
        if (v == null) {
            return;
        }
        this.addSubAnd(q -> {
            q.le(column1, v);  // begin <= cur
            q.ge(column2, v);  // end >= cur
        });
    }


    public void like(String column, String v) {
        if (StrUtil.isEmpty(v)) {
            return;
        }
            this.add(new SpecificationLike<>(column, v.trim()));
    }



    public void notLike(String column, String v) {
        if (StrUtil.isEmpty(v)) {
            return;
        }
        this.add(new SpecificationNotLike<>(column, v));
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


    public void add(Specification<T> spec) {
        if (spec != null) {
            specificationList.add(spec);
        }
    }


    /**
     * 或查询
     * 形如： (a =1 or b =2 or c！=5)
     *
     * @param subQueryConsumer 子查询
     */
    public void addSubOr(Consumer<JpaQuery<T>> subQueryConsumer) {
        JpaQuery<T> q = new JpaQuery<>();
        subQueryConsumer.accept(q);
        this.addSubOr(q.specificationList);
    }

    public void addSubOr(List<Specification<T>> specifications) {
        add((Specification<T>) (root, query, cb) -> {
            Predicate[] predicates = new Predicate[specifications.size()];
            for (int i = 0; i < specifications.size(); i++) {
                predicates[i] = specifications.get(i).toPredicate(root, query, cb);
            }

            return cb.or(predicates);
        });

    }

    public void addSubAnd(Consumer<JpaQuery<T>> subQueryConsumer) {
        JpaQuery<T> q = new JpaQuery<>();
        subQueryConsumer.accept(q);
        this.addSubAnd(q.specificationList);
    }

    public void addSubAnd(List<Specification<T>> specifications) {
        add((Specification<T>) (root, query, cb) -> {
            Predicate[] predicates = new Predicate[specifications.size()];
            for (int i = 0; i < specifications.size(); i++) {
                predicates[i] = specifications.get(i).toPredicate(root, query, cb);
            }

            return cb.and(predicates);
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
