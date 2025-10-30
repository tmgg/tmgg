package io.tmgg.web.persistence.specification;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import java.util.List;

@Deprecated
public interface Selector {


     List<Selection<?>> select(CriteriaBuilder builder, Root<?> root);


}
