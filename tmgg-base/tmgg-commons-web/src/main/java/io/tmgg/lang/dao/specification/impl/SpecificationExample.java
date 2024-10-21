package io.tmgg.lang.dao.specification.impl;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SpecificationExample<T> implements Specification<T> {


    private final Example<T> example;



    public SpecificationExample(T t, String... ignores) {
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
