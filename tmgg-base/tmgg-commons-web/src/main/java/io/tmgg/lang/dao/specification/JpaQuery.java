package io.tmgg.lang.dao.specification;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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


    public void likeExample(T t, String... ignores) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 遇到string，模糊匹配
                .withIgnoreCase()
                .withIgnoreNullValues()
                ;

        if(ignores.length > 0){
            exampleMatcher.withIgnorePaths(ignores);
        }
        Example<T> example = Example.of(t, exampleMatcher);

        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return QueryByExamplePredicateBuilder.getPredicate(root, builder, example);
            }
        });
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
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression<T> expression = getExpression(column, root);
                return builder.equal(expression, v);
            }
        });
    }

    public void isNull(String column) {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return getExpression(column, root).isNull();
            }
        });
    }

    public void isNotNull(String column) {
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return getExpression(column, root).isNotNull();
            }
        });

    }


    public void ne(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression<T> expression = getExpression(column, root);
                return  builder.notEqual(expression, v);
            }
        });
    }


    public void gt(String column, Object v) {
        if (v == null) {
            return;
        }

        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = getExpression(column, root);
                return builder.greaterThan(expression, (Comparable)v);
            }
        });
    }


    public void ge(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = getExpression(column, root);
                return builder.greaterThanOrEqualTo(expression, (Comparable)v);
            }
        });
    }

    public void lt(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = getExpression(column, root);
                return builder.lessThan(expression, (Comparable)v);
            }
        });
    }

    public void le(String column, Object v) {
        if (v == null) {
            return;
        }
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = getExpression(column, root);
                return builder.lessThanOrEqualTo(expression, (Comparable)v);
            }
        });
    }


    /**
     *  包含边界
     * @param column
     * @param v1
     * @param v2
     */
    public void between(String column, Object v1, Object v2) {
        if (v1 == null && v2 == null) {
            return;
        }



        if (v1 != null && v2 != null) {
            this.add(new Specification<T>() {
                @Override
                public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    Expression expression = getExpression(column, root);
                    return builder.between(expression, (Comparable)v1, (Comparable)v2);
                }
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
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = getExpression(column, root);
                return builder.between(expression, (Comparable)v1, (Comparable)v2).not();
            }
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


    public void like(String column, String value) {
        if (StrUtil.isEmpty(value)) {
            return;
        }
        String v = value.trim();
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression<String> expression = getExpression(column, root);


                String likeValue = value.contains("%") ? v : "%" + v + "%";
                return builder.like(expression, likeValue);
            }
        });

    }



    public void notLike(String column, String value) {
        if (StrUtil.isEmpty(value)) {
            return;
        }
        String v = value.trim();
        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression<String> expression = getExpression(column, root);


                String likeValue = value.contains("%") ? v : "%" + v + "%";
                return builder.notLike(expression, likeValue);
            }
        });
    }




    public void in(String column, Object... valueList) {
        this.in(column, ListUtil.of(valueList));
    }

    public void in(String column, Collection<?> valueList) {
        if(CollUtil.isEmpty(valueList)){
            // 阻断查询，查询结果为空
            this.add(new Specification<T>() {
                @Override
                public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    return builder.disjunction();
                }
            });
            return;
        }

        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);


                List<?> list = valueList.stream().filter(Objects::nonNull).toList();
                boolean containsNull = valueList.contains(null);
                if(list.isEmpty() && containsNull){
                    return expression.isNull();
                }

                CriteriaBuilder.In in = builder.in(expression);
                for (Object value : list) {
                    in.value(value);
                }

                // 数据库不会处理null，需按正常逻辑转换下
                return containsNull ? builder.or(in, expression.isNull()) : in;
            }
        });
    }



    public void notIn(String column, Object... valueList) {
        this.notIn(column, ListUtil.of(valueList));
    }


    /***
     *
     * @param column
     * @param valueList 为空时，相当与查所有
     */
    public void notIn(String column, Collection<?> valueList) {
        if(CollUtil.isEmpty(valueList)){
            // 相当于查所有，不做操作
            return;
        }

        this.add(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Expression expression = ExpressionTool.getExpression(column, root);

                List<?> list = valueList.stream().filter(Objects::nonNull).toList();
                boolean hasNull = valueList.contains(null);

                if(list.isEmpty() && hasNull){
                    return expression.isNotNull();
                }

                CriteriaBuilder.In<Object> in = builder.in(expression);
                for (Object value : list) {
                   in.value(value);
                }

                if(!hasNull){ // 正常逻辑要剔除值为null的数据，
                    return  builder.or(in.not(),expression.isNull());
                }
                return in.not();
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


    private Expression getExpression(String column, Root<T> root){
        return ExpressionTool.getExpression(column,root);
    }
}
