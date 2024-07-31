package io.tmgg.lang.dao.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;

public interface Selector {


     List<Selection<?>> select(CriteriaBuilder builder, Root root);


}
