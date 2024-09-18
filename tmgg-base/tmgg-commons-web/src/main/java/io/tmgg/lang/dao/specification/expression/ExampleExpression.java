package io.tmgg.lang.dao.specification.expression;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ExampleExpression<T> implements Specification<T> {


    private final Example<T> example;


    public ExampleExpression(T t) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

        this.example = Example.of(t, exampleMatcher);
    }

    public ExampleExpression(T t, String... ignores) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues()
                ;

        if(ignores.length > 0){
            exampleMatcher.withIgnorePaths(ignores);
        }
        this.example = Example.of(t, exampleMatcher);
    }


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return QueryByExamplePredicateBuilder.getPredicate(root, criteriaBuilder, this.example, EscapeCharacter.DEFAULT);
    }
}
