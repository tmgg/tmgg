package io.tmgg.lang.dao.specification;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import java.util.List;

public interface Selector {


     List<Selection<?>> select(CriteriaBuilder builder, Root<?> root);


}
